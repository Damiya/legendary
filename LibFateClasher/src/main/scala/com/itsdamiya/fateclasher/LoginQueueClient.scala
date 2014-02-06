package com.itsdamiya.fateclasher

import akka.actor._
import com.gvaneyck.rtmp.ServerInfo
import spray.http._
import akka.pattern.ask
import spray.http.HttpRequest
import spray.http.HttpResponse
import scala.util.{Success, Failure}
import scala.concurrent.duration._
import spray.json.DefaultJsonProtocol
import spray.client.pipelining._
import scala.concurrent.Future
import spray.httpx.SprayJsonSupport
import spray.can.Http
import akka.io.IO

case class LoginCmd(user: String, password: String)

case class InGameCredentials(encryptionKey: Option[String], handshakeToken: Option[String], inGame: Boolean, serverIp: Option[String], serverPort: Option[Int], summonerId: Option[Int])

case class LQToken(account_id: Int, account_name: String, fingerprint: String, other: Option[String], resources: Option[String], signature: String, timestamp: Long, uuid: String)

case class LQTicker(id: Int, node: Int, champ: String, current: Int)

case class TokenResponse(lqt: LQToken, status: String)

case class LoginQueueResponse(delay: Int, inGameCredentials: InGameCredentials, lqt: LQToken,
                              rate: Int, reason: String, status: String, user: String, tickers: Option[Array[LQTicker]], node: Option[Int],
                              vcap: Option[Int], backlog: Option[Int], champ: Option[String])

// These values will change, unfortunately
case class TickerResponse(backlog: Int, numOne: Int, numTwo: Int, numThree: Int, numFour: Int, numFive: Int)

trait LoginQueueJsonProtocol extends DefaultJsonProtocol {
  implicit val lqTokenFormat = jsonFormat8(LQToken)
  implicit val ingameCredentialsFormat = jsonFormat6(InGameCredentials)
  implicit val loginQueueTicker = jsonFormat4(LQTicker)
  implicit val loginQueueResponseFormat = jsonFormat12(LoginQueueResponse)
  implicit val tickerResponse = jsonFormat6(TickerResponse)
  implicit val tokenResponse = jsonFormat2(TokenResponse)
}

object LoginQueueClient {
  def apply(server: ServerInfo): Props = Props(classOf[LoginQueueClient], server)
}

class LoginQueueClient(server: ServerInfo) extends Actor with ActorLogging with LoginQueueJsonProtocol {

  implicit val system = context.system

  import context.dispatcher

  import SprayJsonSupport._

  val logRequest: HttpRequest => HttpRequest = { r =>
    log.debug(r.toString); r
  }
  val logResponse: HttpResponse => HttpResponse = { r =>
    log.debug(r.toString); r
  }

  val mapToJson: HttpResponse => HttpResponse = { response =>
    response.withEntity(HttpEntity(ContentTypes.`application/json`, response.entity.data))
  }

  lazy val purePipeline = logRequest ~> sendReceive ~> logResponse ~> mapToJson

  def doLogin(user: String, password: String) = {
    val data = Map("payload" -> s"user=$user,password=$password")

    val pipeline: HttpRequest => Future[LoginQueueResponse] = purePipeline ~> unmarshal[LoginQueueResponse]

    val future: Future[LoginQueueResponse] = pipeline(Post(server.loginQueue + "authenticate", FormData(data)))

    future onComplete {
      case Success(response) =>
        if (response.status == "LOGIN") {
          sender ! response.lqt
          shutdown()
        } else {
          // We can safely get because we know the champ has to be there
          scheduleTickerCheck(response, sender)
        }
      case Failure(t) =>
        log.error(t.toString)
    }
  }

  def checkTicker(lqt: LQToken, rate: Int, delay: Int, champ: String, originalSender: ActorRef) = {
    val pipeline: HttpRequest => Future[TickerResponse] = purePipeline ~> unmarshal[TickerResponse]

    val future: Future[TickerResponse] = pipeline(Get(server.loginQueue + "ticker/" + champ))

    future onComplete {
      case Success(response) =>
        if (response.backlog <= (delay / rate)) {
          self ! RetrieveAuthTokenCmd(lqt, originalSender)
        } else {
          scheduleTickerCheck(InQueue(lqt, rate, delay, champ, originalSender))
        }
      case Failure(t) =>
        log.error(t.toString)
    }
  }

  def scheduleTickerCheck(cmd: InQueue) {
    context.system.scheduler.scheduleOnce(cmd.delay.milliseconds, self, cmd)
  }

  def scheduleTickerCheck(response: LoginQueueResponse, originalSender: ActorRef) {
    scheduleTickerCheck(InQueue(response.lqt, response.rate, response.delay, response.champ.get, originalSender))
  }

  def retrieveAuthToken(lqt: LQToken, originalSender: ActorRef) = {
    val pipeline: HttpRequest => Future[TokenResponse] = purePipeline ~> unmarshal[TokenResponse]
    val future: Future[TokenResponse] = pipeline(Post(server.loginQueue + "token/", lqt))

    future onComplete {
      case Success(response) =>
        if (response.status == "JOIN") {
          originalSender ! response.lqt
          shutdown()
        } else {
          log.error("I haven't seen this case so I can't implement it")
        }
      case Failure(t) =>
        log.error(t.toString)
    }
  }

  def receive: Receive = {
    case LoginCmd(user, password) =>
      doLogin(user, password)
    case InQueue(lqt, rate, delay, champ, originalSender) =>
      checkTicker(lqt, rate, delay, champ, originalSender)
    case RetrieveAuthTokenCmd(lqt, originalSender) =>
      retrieveAuthToken(lqt, originalSender)
  }

  def shutdown() = {
    IO(Http).ask(Http.CloseAll)(1.seconds)
    self ! Kill
  }

  case class RetrieveAuthTokenCmd(lqt: LQToken, originalSender: ActorRef)

  case class InQueue(lqt: LQToken, rate: Int, delay: Int, champ: String, originalSender: ActorRef)

}
