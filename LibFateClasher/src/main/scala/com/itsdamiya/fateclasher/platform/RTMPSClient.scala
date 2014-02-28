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

import com.gvaneyck.rtmp.ServerInfo
import com.itsdamiya.fateclasher.loginqueue.LQToken
import javax.net.ssl.SSLSocketFactory
import java.io.{DataOutputStream, BufferedInputStream, IOException}
import scala.util.Random
import com.typesafe.scalalogging.slf4j.Logging
import com.itsdamiya.fateclasher.platform.rtmp.AMFEncoder
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor._
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.http._
import scala.concurrent.duration._
import spray.httpx.RequestBuilding._
import play.api.libs.json._
import akka.util.Timeout
import scala.collection.concurrent
import com.gvaneyck.rtmp.encoding.TypedObject
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import com.itsdamiya.fateclasher.commands.LoginWithToken
import com.itsdamiya.fateclasher.commands.LoginWithToken
import spray.http.HttpResponse
import com.itsdamiya.fateclasher.loginqueue.LQToken
import javax.net.SocketFactory

object RTMPSClient {
  def apply(targetServer: ServerInfo): Props = Props(classOf[RTMPSClient], targetServer)
}

class RTMPSClient(targetServer: ServerInfo) extends Actor with ActorLogging {
//
//  import context._
//
//  lazy val socket = SSLSocketFactory.getDefault.createSocket(targetServer.hostName, targetServer.getSecurePort)
//  lazy val inputStream = new BufferedInputStream(socket.getInputStream)
//  lazy val outputStream = new DataOutputStream(socket.getOutputStream)
//  lazy val DSId = AMFEncoder.randomUID
//  lazy val rand = new Random()
//  lazy val headers = {
//    val headerObject = new TypedObject()
//    headerObject.put("DSRequestTimeout", Double.box(60))
//    headerObject.put("DSId", DSId)
//    headerObject.put("DSEndpoint", "my-rtmps")
//    headerObject
//  }
//  lazy val invocationMap = new ConcurrentHashMap[Int, Promise[Any]].asScala
//  lazy val packetReader = actorOf(RTMPPacketReader(invocationMap, inputStream), "packetReader")
//  // Start at 2 since we use 1 for connect
//  var invokeID = 2
//
//  def connect(lqToken: LQToken, clientVersion: String, originalSender: ActorRef) {
//    def performHandshake(): Future[Boolean] = {
//      Future {
//        // C0
//        val C0: Byte = 0x03
//        outputStream.write(C0)
//
//        // C1
//        val timestampC1: Long = System.currentTimeMillis
//        val randC1: Array[Byte] = new Array[Byte](1528)
//        rand.nextBytes(randC1)
//        outputStream.writeInt(timestampC1.asInstanceOf[Int])
//        outputStream.writeInt(0)
//        outputStream.write(randC1, 0, 1528)
//        outputStream.flush()
//
//        // S0
//        val S0: Byte = inputStream.read.asInstanceOf[Byte]
//        if (S0 != 0x03) throw new IOException("Server returned incorrect version in handshake: " + S0)
//
//        // S1
//        val S1: Array[Byte] = new Array[Byte](1536)
//        inputStream.read(S1, 0, 1536)
//
//        // C2
//        val timestampS1: Long = System.currentTimeMillis
//        outputStream.write(S1, 0, 4)
//        outputStream.writeInt(timestampS1.asInstanceOf[Int])
//        outputStream.write(S1, 8, 1528)
//        outputStream.flush()
//
//        // S2
//        val S2: Array[Byte] = new Array[Byte](1536)
//        inputStream.read(S2, 0, 1536)
//
//        for (i <- 8 until 1536) {
//          if (randC1(i - 8) != S2(i)) {
//            false
//          }
//        }
//        true
//      }
//    }
//
//    def createConnectPacket() {
//      val params = Map[String, Any]() +
//        ("app" -> "") +
//        ("flashVer" -> "WIN 10,1,85,3") +
//        ("swfUrl" -> "app:/mod_ser.dat") +
//        ("tcUrl" -> s"rtmps://${targetServer.hostName}:${targetServer.getSecurePort}") +
//        ("fpad" -> false) +
//        ("capabilities" -> 239) +
//        ("audioCodecs" -> 3191) +
//        ("videoCodecs" -> 252) +
//        ("videoFunction" -> 1) +
//        ("pageUrl" -> null) +
//        ("objectEncoding" -> 3)
//
//      AMFEncoder.encodeConnect(params)
//    }
//
//    def retrieveLoginIpAddress(): Future[String] = {
//      implicit val timeout = Timeout(10.seconds)
//      val httpResponse = (IO(Http) ? Get("http://ll.leagueoflegends.com/services/connection_info")).mapTo[HttpResponse]
//      httpResponse.collect {
//        case response: HttpResponse =>
//          val jsonResponse = Json.parse(response.entity.asString)
//          (jsonResponse \ "ip_address").as[String]
//      }
//    }
//
//    def performPlatformLogin(ipAddress: String): Future[Any] = {
//      val body = new TypedObject("com.riotgames.platform.login.AuthenticationCredentials")
//      body.put("username", lqToken.account_name)
//      body.put("clientVersion", clientVersion)
//      body.put("ipAddress", ipAddress)
//      body.put("locale", "en_US")
//      body.put("domain", "lolclient.lol.riotgames.com")
//      body.put("operatingSystem", "LegendaryClient")
//      body.put("securityAnswer", null)
//      body.put("oldPassword", null)
//      body.put("partnerCredentials", null)
//      invoke("loginService", "login", body)
//    }
//
//    val platformLoginFuture = for {
//      handshakeSucceeded <- performHandshake()
//      ipAddress <- retrieveLoginIpAddress()
//      if handshakeSucceeded
//    } yield performPlatformLogin(ipAddress)
//
//  }
//
//  def invoke(wrappedObject: TypedObject): Future[Any] = {
//    val promise = Promise[Any]()
//    invocationMap.put(invokeID, promise)
//
//    try {
//      val invokeBytes = AMFEncoder.encodeInvoke(invokeID, wrappedObject)
//      outputStream.write(invokeBytes, 0, invokeBytes.length)
//      outputStream.flush()
//    } catch {
//      case exception: IOException =>
//        promise.failure(exception)
//        log.error(s"Invoke failed! ${exception.getMessage}")
//    }
//    invokeID += 1
//
//    promise.future
//  }
//
//  def invoke(service: String, method: String, messageBody: AnyRef): Future[Any] = {
//    invoke(wrapBody(service, method, messageBody))
//  }
//
//  private def wrapBody(service: String, method: String, messageBody: AnyRef): TypedObject = {
//    val returnObject = new TypedObject("flex.messaging.messages.RemotingMessage")
//    returnObject.put("destination", service)
//    returnObject.put("operation", method)
//    returnObject.put("source", null)
//    returnObject.put("timestamp", Int.box(0))
//    returnObject.put("messageId", AMFEncoder.randomUID)
//    returnObject.put("timeToLive", Int.box(0))
//    returnObject.put("clientId", null)
//    returnObject.put("headers", headers)
//    returnObject.put("body", messageBody)
//
//    returnObject
//  }

  def receive = {
    case LoginWithToken(lqToken, clientVersion) =>
      val originalSender = sender()
      //connect(lqToken, clientVersion, originalSender)
  }
}
