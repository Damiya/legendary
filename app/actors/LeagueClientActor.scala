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
import akka.util.Timeout
import com.achimala.leaguelib.connection.{LeagueServer, LeagueAccount}
import com.achimala.leaguelib.errors.LeagueException
import play.Logger

object ConnectionStatus {
  val LOGGED_OUT = "loggedOut"
  val LOGGED_IN = "loggedIn"
  val LOGIN_FORBIDDEN = "loggedForbidden"
  val IN_GAME = "inGame"
}

trait LeagueClient {
  def login(info: UserPass): Future[String]
  def isConnected: Boolean
  def logout(): Unit
}

class LeagueClientImpl extends LeagueClient {
  val client = new LeagueAccount(LeagueServer.NORTH_AMERICA, "4.1.14_01_15_16_01", null, null)

  implicit val ec: ExecutionContext = TypedActor.context.dispatcher

  def login(info: UserPass): Future[String] = {
    client.setUsername(info.username)
    client.setPassword(info.password)


    val connectionResult = Promise[String]()
    Logger.warn("Connection requested for " + info.username)

    Future {
      try {
        Logger.warn("Connection trying")
        client.connect()
        while (!client.isConnected) {
          Logger.warn("Connection sleeping")
          Thread.sleep(5000)
        }
        Logger.info("Connection succeeded")
        connectionResult.success(ConnectionStatus.LOGGED_IN)
      } catch {
        case exception: LeagueException =>
          connectionResult.failure(exception)
          Logger.error(exception.toString)
      }

    }
    connectionResult.future
  }

  def isConnected:Boolean = {
    return client.isConnected
  }

  def logout(): Unit = {
    if (client.isConnected) {
      Logger.info("Logging out " + client.getUsername)
      client.close()
    } else {
      Logger.error("Client not logged in!")
    }
  }
}
