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

package com.itsdamiya.fateclasher.platform

import akka.actor.{Props, Actor, ActorLogging}
import akka.io.{IO, Tcp}
import akka.io.Tcp._
import com.itsdamiya.fateclasher.loginqueue.LQToken
import com.gvaneyck.rtmp.ServerInfo

object PlatformClient {
  def apply(lqt: LQToken, targetServer: ServerInfo): Props = Props(classOf[PlatformClient], lqt, targetServer)
}

class PlatformClient(lqt: LQToken, targetServer: ServerInfo) extends Actor with ActorLogging with SSLAdditions {

  import context.system

  IO(Tcp) ! Connect(targetServer.getPlatformAddress)

  def receive: Receive = {
    case Connected(remote, _) =>
      val connection = sender()
      connection ! Register(self)
      log.debug("Connected! Woohoo")
    //      val init = TcpPipelineHandler.withLogger(log,
    //        new StringByteStringAdapter("utf-8") >>
    //          new DelimiterFraming(maxSize = 1024, delimiter = ByteString('\n'),
    //            includeDelimiter = true) >>
    //          new TcpReadWriteAdapter >>
    //          new SslTlsSupport(getSSLEngine(remote, server)) >>
    //          new BackpressureBuffer(lowBytes = 100, highBytes = 1000, maxBytes = 1000000))

    //      val connection = sender
    //      val handler = context.actorOf(Props(new AkkaSslHandler(init)).withDeploy(Deploy.local))
    //      val pipeline = context.actorOf(TcpPipelineHandler.props(
    //        init, connection, handler).withDeploy(Deploy.local))
    //
    //      connection ! Tcp.Register(pipeline)
  }

}


//class AkkaSslHandler(init: Init[WithinActorContext, String, String])
//  extends Actor with ActorLogging {
//
//  def receive = {
//    case init.Event(data) =>
//      val input = data.dropRight(1)
//      log.debug("akka-io Server received {} from {}", input, sender)
//      val response = "butts"
//      sender ! init.Command(response)
//      log.debug("akka-io Server sent: {}", response.dropRight(1))
//    case _: Tcp.ConnectionClosed ⇒ context.stop(self)
//  }
//}
