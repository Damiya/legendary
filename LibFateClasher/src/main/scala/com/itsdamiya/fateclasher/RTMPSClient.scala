package com.itsdamiya.fateclasher

import com.gvaneyck.rtmp.{SavingTrustManager, ServerInfo}
import akka.io._
import javax.net.ssl._
import java.net.InetSocketAddress
import java.security.KeyStore
import java.io.{FileInputStream, File}
import akka.util.ByteString
import akka.io.TcpPipelineHandler.{Init, WithinActorContext}
import akka.actor.{Deploy, ActorLogging, Actor, Props}

object RTMPSClient {
  def props(server: ServerInfo): Props = Props(classOf[RTMPSClient], server)
}

class RTMPSClient(server: ServerInfo) extends Actor with ActorLogging {

  import Tcp._
  import context.system

  IO(Tcp) ! Connect(server.getSocketAddress)

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
    ctx.init(null, Array(trustManager), null)
    ctx.createSSLEngine(server.hostName, 2099)
  }

  def receive: Receive = {
    case Connected(remote, _) ⇒
          val init = TcpPipelineHandler.withLogger(log,
            new StringByteStringAdapter("utf-8") >>
              new DelimiterFraming(maxSize = 1024, delimiter = ByteString('\n'),
                includeDelimiter = true) >>
              new TcpReadWriteAdapter >>
              new SslTlsSupport(getSSLEngine(remote, server)) >>
              new BackpressureBuffer(lowBytes = 100, highBytes = 1000, maxBytes = 1000000))

          val connection = sender
          val handler = context.actorOf(Props(new AkkaSslHandler(init)).withDeploy(Deploy.local))
          val pipeline = context.actorOf(TcpPipelineHandler.props(
            init, connection, handler).withDeploy(Deploy.local))

          connection ! Tcp.Register(pipeline)
  }
}

class AkkaSslHandler(init: Init[WithinActorContext, String, String])
  extends Actor with ActorLogging {

  def receive = {
    case init.Event(data) ⇒
      val input = data.dropRight(1)
      log.debug("akka-io Server received {} from {}", input, sender)
      val response = "butts"
      sender ! init.Command(response)
      log.debug("akka-io Server sent: {}", response.dropRight(1))
    case _: Tcp.ConnectionClosed ⇒ context.stop(self)
  }
}