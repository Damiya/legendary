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

import com.gvaneyck.rtmp.{RTMPSClient, ServerInfo}
import com.itsdamiya.fateclasher.loginqueue.LQToken
import javax.net.ssl.SSLSocketFactory
import java.io.{DataOutputStream, BufferedInputStream, IOException}
import scala.util.Random
import com.typesafe.scalalogging.slf4j.Logging
import com.itsdamiya.fateclasher.platform.rtmp.AMFEncoder

class RTMPClient(targetServer: ServerInfo, clientVersion: String, lqToken: LQToken) extends Logging {
  lazy val socket = SSLSocketFactory.getDefault.createSocket(targetServer.hostName, targetServer.getPort)
  lazy val inputStream = new BufferedInputStream(socket.getInputStream)
  lazy val outputStream = new DataOutputStream(socket.getOutputStream)
  lazy val rand = new Random()

  def connect() {
    def doHandshake() {
      // C0
      val C0: Byte = 0x03
      outputStream.write(C0)

      // C1
      val timestampC1: Long = System.currentTimeMillis
      val randC1: Array[Byte] = new Array[Byte](1528)
      rand.nextBytes(randC1)
      outputStream.writeInt(timestampC1.asInstanceOf[Int])
      outputStream.writeInt(0)
      outputStream.write(randC1, 0, 1528)
      outputStream.flush()

      // S0
      val S0: Byte = inputStream.read.asInstanceOf[Byte]
      if (S0 != 0x03) throw new IOException("Server returned incorrect version in handshake: " + S0)

      // S1
      val S1: Array[Byte] = new Array[Byte](1536)
      inputStream.read(S1, 0, 1536)

      // C2
      val timestampS1: Long = System.currentTimeMillis
      outputStream.write(S1, 0, 4)
      outputStream.writeInt(timestampS1.asInstanceOf[Int])
      outputStream.write(S1, 8, 1528)
      outputStream.flush()

      // S2
      val S2: Array[Byte] = new Array[Byte](1536)
      inputStream.read(S2, 0, 1536)

      // Validate handshake
      def valid: Boolean = {
        for (i <- 8 until 1536) {
          if (randC1(i - 8) != S2(i)) {
            false
          }
        }
        true
      }

      if (!valid) throw new IOException("Server returned invalid handshake")
    }
    //doHandshake()

    val params = Map[String, Any]() +
      ("app" -> "") +
      ("flashVer" -> "WIN 10,1,85,3") +
      ("swfUrl" -> "app:/mod_ser.dat") +
      ("tcUrl" -> s"rtmps://${targetServer.hostName}:${targetServer.getPort}") +
      ("fpad" -> false) +
      ("capabilities" -> 239) +
      ("audioCodecs" -> 3191) +
      ("videoCodecs" -> 252) +
      ("videoFunction" -> 1) +
      ("pageUrl" -> null) +
      ("objectEncoding" -> 3)

    val connect = AMFEncoder.encodeConnect(params).toList.toString()
  }
}
