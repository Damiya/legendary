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

package com.itsdamiya.fateclasher

import com.gvaneyck.rtmp.ServerInfo
import java.io.File
import akka.actor.{ActorLogging, Actor, Props}
import com.itsdamiya.fateclasher.loginqueue.{LQToken, LoginQueueClient}
import com.itsdamiya.fateclasher.commands.LoginWithCredentials
import com.itsdamiya.fateclasher.events.LoginWithCredentialsComplete
import akka.event.LoggingReceive

object LeagueClientSupervisor {
  def apply(): Props = Props(classOf[LeagueClientSupervisor])
}

class LeagueClientSupervisor extends Actor with ActorLogging {

  import context._

  def receive: Receive = LoggingReceive {
    // Commands from outside
    case LoginWithCredentials(username, password, targetServer) =>
      obtainLoginToken(username, password, targetServer)

    // Events from children
    case LoginWithCredentialsComplete(lqt, targetServer) =>
      performPlatformLogin(lqt, targetServer)
  }

  def obtainLoginToken(username: String, password: String, targetServer: ServerInfo) {
    val loginQueueClient = actorOf(LoginQueueClient(), "loginQueue")
    loginQueueClient ! LoginWithCredentials(username, password, targetServer)
  }

  def performPlatformLogin(lqt: LQToken, targetServer: ServerInfo) {
    // Wind down the loginQueue actor since we're moving on to the platform
    stop(child("loginQueue").get)
  }
}
