/*
 * Copyright 2014 Kate von Roeder (katevonroder at gmail dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itsdamiya.fateclasher.loginqueue

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import com.gvaneyck.rtmp.ServerInfo
import com.itsdamiya.fateclasher.utils.HTTPTransformers
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import spray.can.Http
import spray.client.pipelining._
import spray.http._
import com.itsdamiya.fateclasher.commands.LoginWithCredentials
import com.itsdamiya.fateclasher.events.LoginWithCredentialsComplete
import akka.event.LoggingReceive


/**
 * Props generator for LoginQueueClient actors
 */
object LoginQueueClient {
  def apply(targetServer: ServerInfo): Props = Props(classOf[LoginQueueClient], targetServer)
}

/**
 * Fully contained Actor for interacting with the Riot LoginQueue
 *
 * @see com.itsdamiya.fateclasher.loginqueue.LoginWithCredentials
 */
class LoginQueueClient(targetServer: ServerInfo) extends Actor with ActorLogging with LoginQueueProtocol with HTTPTransformers {

  implicit val system = context.system

  import context.dispatcher

  // Pipeline object to handle all calls
  private lazy val purePipeline = logRequest ~> sendReceive ~> logResponse ~> mapToJson

  private case object ShutdownCommand

  /**
   * Schedule another ticker update with a pollticker command
   * @param command Command with relevant ticker update information (and timing)
   */
  private def scheduleTickerCheck(command: CheckTickerCommand) {
    context.system.scheduler.scheduleOnce(command.delay.milliseconds, self, command)
  }

  /**
   * Schedule a ticker update in response to a queue when authenticating to the Login service
   * @param response Response from the LQ
   * @param originalSender ActorRef pointing to the actor that requested a Login Token
   */
  private def scheduleTickerCheck(response: AuthenticateResponse, originalSender: ActorRef) {
    scheduleTickerCheck(CheckTickerCommand(response.lqt, response.rate, response.delay, response.champ.get, originalSender))
  }

  /**
   * Kick off the login process
   * @param username League username
   * @param password League password
   * @param originalSender ActorRef pointing to the actor that requested a Login Token
   */
  private def login(username: String, password: String, originalSender: ActorRef) {
    // We take an originalSender parameter to ensure that we're protected from issues with mutable state by the time the future completes
    // See http://doc.akka.io/docs/akka/2.2.3/general/jmm.html#jmm-shared-state for more details

    val data: FormData = FormData(Map("payload" -> s"user=$username,password=$password"))

    val pipeline: HttpRequest => Future[AuthenticateResponse] = purePipeline ~> unmarshal[AuthenticateResponse]

    val httpFuture: Future[AuthenticateResponse] = pipeline(
      Post(targetServer.loginQueue + "authenticate", data)
    )

    httpFuture onComplete {
      case Success(response) =>
        if (response.status == "LOGIN") {
          originalSender ! LoginWithCredentialsComplete(response.lqt)
        } else {
          // We hit a queue, go into monitoring mode
          scheduleTickerCheck(response, originalSender)
        }

      case Failure(exception) =>
        originalSender ! Status.Failure(exception)
        log.error(exception.toString)
    }
  }

  /**
   * Poll the appropriate LQ Ticker for an update to the backlog
   * @param lqt Partially signed LQ Token
   * @param rate Queue drain rate
   * @param delay Requested client update rate
   * @param champ Ticker name
   * @param originalSender ActorRef pointing to the actor that requested a Login Token
   */
  private def checkTicker(lqt: LQToken, rate: Int, delay: Int, champ: String, originalSender: ActorRef) {
    val pipeline: HttpRequest => Future[TickerResponse] = purePipeline ~> unmarshal[TickerResponse]

    val httpFuture: Future[TickerResponse] = pipeline(
      Get(targetServer.loginQueue + "ticker/" + champ)
    )

    httpFuture onComplete {
      case Success(response) =>
        // Mathematically we should be about at the front (since we just waited a full delay)
        if (response.backlog <= (delay / rate)) {
          self ! RetrieveAuthTokenCommand(lqt, originalSender)
        } else {
          // Still in queue
          scheduleTickerCheck(CheckTickerCommand(lqt, rate, delay, champ, originalSender))
        }

      case Failure(exception) =>
        originalSender ! Status.Failure(exception)
        log.error(exception.toString)
    }
  }

  /**
   * Invoked after getting to the end of the LQ; retrieves a signed LQToken for League of Legends
   * @param lqt Partially signed LQ Token
   * @param originalSender ActorRef pointing to the actor that requested a Login Token
   */
  private def retrieveAuthToken(lqt: LQToken, originalSender: ActorRef) {
    val pipeline: HttpRequest => Future[AuthTokenResponse] = purePipeline ~> unmarshal[AuthTokenResponse]

    val httpFuture: Future[AuthTokenResponse] = pipeline(Post(targetServer.loginQueue + "token/", lqt))

    httpFuture onComplete {
      case Success(response) =>
        // Join indicates that we are in fact ready to merge over to the League platform
        if (response.status == "JOIN") {
          originalSender ! LoginWithCredentialsComplete(response.lqt)
        } else {
          log.error("I haven't seen this case so I can't implement it")
        }

      case Failure(exception) =>
        originalSender ! Status.Failure(exception)
        log.error(exception.toString)
    }
  }

  def receive: Receive = LoggingReceive {
    case LoginWithCredentials(username, password) =>
      val originalSender = sender()
      login(username, password, originalSender)
    case CheckTickerCommand(lqt, rate, delay, champ, originalSender) =>
      checkTicker(lqt, rate, delay, champ, originalSender)
    case RetrieveAuthTokenCommand(lqt, originalSender) =>
      retrieveAuthToken(lqt, originalSender)
  }
}

