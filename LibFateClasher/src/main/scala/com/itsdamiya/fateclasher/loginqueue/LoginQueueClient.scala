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

case class LoginCommand(username: String, password: String, targetServer: ServerInfo)

/**
 * Props generator for LoginQueueClient actors
 */
object LoginQueueClient {
  def apply(): Props = Props(classOf[LoginQueueClient])
}

/**
 * Fully contained Actor for interacting with the Riot LoginQueue
 *
 * @see com.itsdamiya.fateclasher.loginqueue.LoginCommand
 */
class LoginQueueClient extends Actor with ActorLogging with LoginQueueProtocol with HTTPTransformers {

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
   * @param targetServer Target League server
   * @param response Response from the LQ
   * @param originalSender ActorRef pointing to the actor that requested a Login Token
   */
  private def scheduleTickerCheck(targetServer: ServerInfo, response: AuthenticateResponse, originalSender: ActorRef) {
    scheduleTickerCheck(CheckTickerCommand(targetServer, response.lqt, response.rate, response.delay, response.champ.get, originalSender))
  }

  /**
   * Kick off the login process
   *
   * @param command Received LoginCommand
   * @param originalSender ActorRef pointing to the actor that requested a Login Token
   */
  private def login(command: LoginCommand, originalSender: ActorRef) {
    // We take an originalSender parameter to ensure that we're protected from issues with mutable state by the time the future completes
    // See http://doc.akka.io/docs/akka/2.2.3/general/jmm.html#jmm-shared-state for more details

    val data: FormData = FormData(Map("payload" -> s"user=${command.username},password=${command.password}"))

    val pipeline: HttpRequest => Future[AuthenticateResponse] = purePipeline ~> unmarshal[AuthenticateResponse]

    val httpFuture: Future[AuthenticateResponse] = pipeline(
      Post(command.targetServer.loginQueue + "authenticate", data)
    )

    httpFuture onComplete {
      case Success(response) =>
        if (response.status == "LOGIN") {
          originalSender ! response.lqt
          self ! ShutdownCommand
        } else {
          // We hit a queue, go into monitoring mode

          scheduleTickerCheck(command.targetServer, response, originalSender)
        }

      case Failure(exception) =>
        originalSender ! Status.Failure(exception)
        log.error(exception.toString)
    }
  }

  /**
   * Poll the appropriate LQ Ticker for an update to the backlog
   * @param command As named
   */
  private def checkTicker(command: CheckTickerCommand) {
    val pipeline: HttpRequest => Future[TickerResponse] = purePipeline ~> unmarshal[TickerResponse]

    val httpFuture: Future[TickerResponse] = pipeline(
      Get(command.targetServer.loginQueue + "ticker/" + command.champ)
    )

    httpFuture onComplete {
      case Success(response) =>
        // Mathematically we should be about at the front (since we just waited a full delay)
        if (response.backlog <= (command.delay / command.rate)) {
          self ! RetrieveAuthTokenCommand(command.targetServer, command.lqt, command.originalSender)
        } else {
          // Still in queue
          scheduleTickerCheck(command)
        }

      case Failure(exception) =>
        command.originalSender ! Status.Failure(exception)
        log.error(exception.toString)
    }
  }

  /**
   * Invoked after getting to the end of the LQ; retrieves a signed LQToken for League of Legends
   * @param command As named
   */
  private def retrieveAuthToken(command: RetrieveAuthTokenCommand) {
    val pipeline: HttpRequest => Future[AuthTokenResponse] = purePipeline ~> unmarshal[AuthTokenResponse]

    val httpFuture: Future[AuthTokenResponse] = pipeline(Post(command.targetServer.loginQueue + "token/", command.lqt))

    httpFuture onComplete {
      case Success(response) =>
        // Join indicates that we are in fact ready to merge over to the League platform
        if (response.status == "JOIN") {
          command.originalSender ! response.lqt
          shutdown()
        } else {
          log.error("I haven't seen this case so I can't implement it")
        }

      case Failure(exception) =>
        command.originalSender ! Status.Failure(exception)
        log.error(exception.toString)
    }
  }

  def receive: Receive = {
    case command: LoginCommand =>
      val originalSender = sender
      login(command, originalSender)
    case command: CheckTickerCommand =>
      checkTicker(command)
    case command: RetrieveAuthTokenCommand =>
      retrieveAuthToken(command)
    case ShutdownCommand =>
      shutdown()
  }

  /**
   * Clean up any remaining HTTP sockets and terminate the actor.
   */
  def shutdown() {
    IO(Http).ask(Http.CloseAll)(1.seconds)
    self ! Kill
  }
}

