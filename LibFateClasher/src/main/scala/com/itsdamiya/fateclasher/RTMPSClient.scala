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

import com.gvaneyck.rtmp.{SavingTrustManager, ServerInfo}
import akka.io._
import javax.net.ssl._
import java.net.InetSocketAddress
import java.security.KeyStore
import java.io.{FileInputStream, File}
import akka.actor.{ActorLogging, Actor, Props}
import akka.io.Tcp._

object RTMPSClient {
  def props(server: ServerInfo): Props = Props(classOf[RTMPSClient], server)
}

class RTMPSClient(server: ServerInfo) extends Actor with ActorLogging {

  import context.system

  private val serverSocket = server.getSocketAddress

  IO(Tcp) ! Connect(serverSocket)

  def getSSLEngine(remote: InetSocketAddress, server: ServerInfo): SSLEngine = {
    def createSavingTrustManager: SavingTrustManager = {
      val keyStore = KeyStore.getInstance(KeyStore.getDefaultType)
      var file = new File("certs/" + server.hostName + ".cert")
      if (!file.exists || !file.isFile) {
        file = new File(System.getProperty("java.home") + "/lib/security/cacerts")
      }
      keyStore.load(new FileInputStream(file), "changeit".toCharArray)

      val trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
      trustFactory.init(keyStore)
      val trustManager = new SavingTrustManager(trustFactory.getTrustManagers()(0).asInstanceOf[X509TrustManager])

      trustManager
    }

    val trustManager = createSavingTrustManager
    val ctx = SSLContext.getInstance("TLS")

    //scalastyle:off null
    ctx.init(null, Array(trustManager), null)
    //scalastyle:on null

    ctx.createSSLEngine(serverSocket.getHostString, serverSocket.getPort)
  }

  def receive: Receive = {
    case Connected(remote, _) =>
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
