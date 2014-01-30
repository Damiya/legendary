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

package actors

import scala.concurrent.{ExecutionContext, Promise, Future, Await}
import scala.concurrent.duration._
import akka.actor._
import models.UserPass
import play.Logger

object ConnectionStatus {
  val LOGGED_OUT = "loggedOut"
  val LOGGED_IN = "loggedIn"
  val LOGIN_FORBIDDEN = "loginForbidden"
  val IN_GAME = "inGame"
}

trait LeagueClient {
  def login(info: UserPass): Future[String]
  def isConnected: Boolean
  def logout(): Unit
}

class LeagueClientImpl extends LeagueClient {

  implicit val ec: ExecutionContext = TypedActor.context.dispatcher

  def login(info: UserPass): Future[String] = {


    val connectionResult = Promise[String]()
    Logger.warn("Connection requested for " + info.username)

    Future {
      try {
        connectionResult.success(ConnectionStatus.LOGGED_IN)
      } catch {
        case exception: Exception =>
          connectionResult.failure(exception)
          Logger.error(exception.toString)
      }

    }
    connectionResult.future
  }

  def isConnected:Boolean = {
     true
  }

  def logout(): Unit = {

  }
}
