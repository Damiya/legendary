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

import akka.actor.{Actor, ActorLogging, Props}
import java.io.{BufferedInputStream, InputStream}
import scala.concurrent.Promise
import scala.collection.mutable

object RTMPPacketReader {
  def apply(invocationMap: mutable.Map[Int, Promise[Any]], stream: InputStream): Props = Props(classOf[RTMPPacketReader], invocationMap, stream)
}

class RTMPPacketReader(invocationMap: mutable.Map[Int, Promise[Any]], stream: InputStream) extends Actor with ActorLogging {
  // TODO: 16384 size is from Gabe's code, why did he choose that particular size?
  lazy val inputStream = new BufferedInputStream(stream, 16384)

  import context._

  def receive = ???
}
