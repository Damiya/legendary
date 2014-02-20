// Based on AMF3Encoder.java by Gabriel van Eyck; ported to Scala by Kate von Roeder

package com.itsdamiya.fateclasher.platform.rtmp

import com.gvaneyck.rtmp.encoding.{EncodingException, TypedObject, AMF3Encoder}
import java.nio.ByteBuffer
import java.util.Date
import scala.collection.{immutable, mutable}
import scala.collection.convert.WrapAsJava
import com.typesafe.scalalogging.slf4j.Logging
import scala.util.Random

object AMFEncoder extends WrapAsJava with Logging {
  private lazy val startTime = System.currentTimeMillis()

  def encodeConnect(connectionMap: immutable.Map[String, Any]): Array[Byte] = {
    val byteList = mutable.MutableList[Byte]()

    writeStringAMF0(byteList, "connect")
    writeIntAMF0(byteList, 1)

    // Write params
    byteList += 0x11.asInstanceOf[Byte]
    byteList += 0x09.asInstanceOf[Byte]
    writeMap(byteList, mutable.Map(connectionMap.toSeq: _*))

    // Write service call args
    byteList += 0x01.asInstanceOf[Byte]
    byteList += 0x00.asInstanceOf[Byte]

    writeStringAMF0(byteList, "nil")
    writeStringAMF0(byteList, "")

    // Set up CommandMessage
    val commandMessage = new TypedObject("flex.messaging.messages.CommandMessage")
    commandMessage.put("messageRefType", null)
    commandMessage.put("operation", Int.box(5))
    commandMessage.put("correlationId", "")
    commandMessage.put("clientId", null)
    commandMessage.put("destination", "")
    commandMessage.put("messageId", randomUID)
    commandMessage.put("timestamp", Double.box(0))
    commandMessage.put("timeToLive", Double.box(0))
    commandMessage.put("body", new TypedObject)

    val headers = mutable.Map[String, AnyRef]()
    headers.put("DSMessagingVersion", Double.box(1d))
    headers.put("DSId", "my-rtmps")
    commandMessage.put("headers", headers)

    // Write CommandMessage
    byteList += 0x11.asInstanceOf[Byte]
    encode(byteList, commandMessage)

    val returnValue = finalizeData(byteList)
    returnValue(7) = 0x14.asInstanceOf[Byte]

    returnValue
  }

  def encodeInvoke(invokeId: Int, data: Any): Array[Byte] = {
    val result = mutable.MutableList[Byte]()

    result += 0x00.asInstanceOf[Byte]
    result += 0x05.asInstanceOf[Byte]
    writeIntAMF0(result, invokeId)
    result += 0x05.asInstanceOf[Byte]
    result += 0x11.asInstanceOf[Byte]
    encode(result, data)

    val returnValue = finalizeData(result)

    returnValue
  }

  private def encode(buffer: mutable.MutableList[Byte], value: Any) {
    if (value == null) {
      buffer += 0x01.asInstanceOf[Byte]
    }
    else value match {
      case asBoolean: Boolean =>
        if (asBoolean) buffer += 0x03.asInstanceOf[Byte]
        else buffer += 0x02.asInstanceOf[Byte]
      case integer: Integer =>
        buffer += 0x04.asInstanceOf[Byte]
        writeInt(buffer, integer)
      case double: Double =>
        buffer += 0x05.asInstanceOf[Byte]
        writeDouble(buffer, double)
      case string: String =>
        buffer += 0x06.asInstanceOf[Byte]
        writeString(buffer, string)
      case date: Date =>
        buffer += 0x08.asInstanceOf[Byte]
        writeDate(buffer, date)
      case array: Array[Any] =>
        buffer += 0x09.asInstanceOf[Byte]
        writeArray(buffer, array)
      case typedObject: TypedObject =>
        buffer += 0x0A.asInstanceOf[Byte]
        writeObject(buffer, typedObject)
      case map: mutable.Map[_, _] =>
        buffer += 0x09.asInstanceOf[Byte]
        writeMap(buffer, map)
      case _ =>
        throw new EncodingException("Unexpected object objectType: " + value.getClass.getName)
    }
  }

  private def finalizeData(data: mutable.MutableList[Byte]): Array[Byte] = {
    val result = mutable.MutableList[Byte]()

    result += 0x03.asInstanceOf[Byte]

    // Timestamp
    val timediff: Long = System.currentTimeMillis - startTime
    result += ((timediff & 0xFF0000) >> 16).asInstanceOf[Byte]
    result += ((timediff & 0x00FF00) >> 8).asInstanceOf[Byte]
    result += (timediff & 0x0000FF).asInstanceOf[Byte]

    // Body size
    result += ((data.length & 0xFF0000) >> 16).asInstanceOf[Byte]
    result += ((data.length & 0x00FF00) >> 8).asInstanceOf[Byte]
    result += (data.length & 0x0000FF).asInstanceOf[Byte]

    // Content objectType
    result += 0x11.asInstanceOf[Byte]

    // Source ID
    result += 0x00.asInstanceOf[Byte]
    result += 0x00.asInstanceOf[Byte]
    result += 0x00.asInstanceOf[Byte]
    result += 0x00.asInstanceOf[Byte]

    // Add body
    for (i <- 0 until data.length) {
      result += data(i)
      if (i % 128 == 127 && i != data.length - 1) {
        result += 0xC3.asInstanceOf[Byte]
      }
    }

    result.toArray
  }

  private def writeIntAMF0(buffer: mutable.MutableList[Byte], value: Int) {
    buffer += 0x00.asInstanceOf[Byte]

    writeDouble(buffer, value.toDouble)
  }

  private def writeInt(buffer: mutable.MutableList[Byte], value: Int) {
    if (value < 0 || value >= 0x200000) {
      buffer += (value >> 22 & 0x7f | 0x80).asInstanceOf[Byte]
      buffer += (value >> 15 & 0x7f | 0x80).asInstanceOf[Byte]
      buffer += ((value >> 8).&(0x7f) | 0x80).asInstanceOf[Byte]
      buffer += (value & 0xff).asInstanceOf[Byte]
    }
    else {
      if (value >= 0x4000) {
        buffer += (((value >> 14) & 0x7f) | 0x80).asInstanceOf[Byte]
      }
      if (value >= 0x80) {
        buffer += (((value >> 7) & 0x7f) | 0x80).asInstanceOf[Byte]
      }
      buffer += (value & 0x7f).asInstanceOf[Byte]
    }
  }

  private def writeString(buffer: mutable.MutableList[Byte], value: String) {
    val stringAsBytes = value.getBytes("UTF-8")
    writeInt(buffer, (stringAsBytes.length << 1) | 1)

    stringAsBytes.foreach(byte => buffer += byte)
  }

  private def writeDouble(buffer: mutable.MutableList[Byte], value: Double) {
    if (value.isNaN) {
      buffer += 0x7F.asInstanceOf[Byte]
      buffer += 0xFF.asInstanceOf[Byte]
      buffer += 0xFF.asInstanceOf[Byte]
      buffer += 0xFF.asInstanceOf[Byte]
      buffer += 0xE0.asInstanceOf[Byte]
      buffer += 0x00.asInstanceOf[Byte]
      buffer += 0x00.asInstanceOf[Byte]
      buffer += 0x00.asInstanceOf[Byte]
    } else {
      val temp: Array[Byte] = new Array[Byte](8)
      ByteBuffer.wrap(temp).putDouble(value)
      temp.foreach(byte => buffer += byte)
    }
  }

  private def writeStringAMF0(buffer: mutable.MutableList[Byte], value: String) {
    val stringAsBytes = value.getBytes("UTF-8")

    buffer += 0x02.asInstanceOf[Byte]
    buffer += ((stringAsBytes.length & 0xFF00) >> 8).asInstanceOf[Byte]
    buffer += (stringAsBytes.length & 0x00FF).asInstanceOf[Byte]

    stringAsBytes.foreach(byte => buffer += byte)
  }

  private def writeMap(buffer: mutable.MutableList[Byte], map: mutable.Map[_, _]) {
    buffer += 0x01.asInstanceOf[Byte]

    map.foreach {
      case (key, value) =>
        writeString(buffer, key.asInstanceOf[String])
        encode(buffer, value)
    }

    buffer += 0x01.asInstanceOf[Byte]
  }

  private def writeArray(buffer: mutable.MutableList[Byte], value: Array[Any]) {
    writeInt(buffer, (value.length << 1) | 1)
    buffer += 0x01.asInstanceOf[Byte]
    value.foreach(item => encode(buffer, item))
  }

  private def writeDate(buffer: mutable.MutableList[Byte], value: Date) {
    buffer += 0x01.asInstanceOf[Byte]
    writeDouble(buffer, value.getTime)
  }

  private def writeObject(buffer: mutable.MutableList[Byte], value: TypedObject) {
    value.objectType match {
      case null | "" =>
        buffer += 0x0B.asInstanceOf[Byte]
        buffer += 0x01.asInstanceOf[Byte]
        import scala.collection.JavaConversions._
        for (key <- value.keySet) {
          writeString(buffer, key)
          encode(buffer, value.get(key))
        }
        buffer += 0x01.asInstanceOf[Byte]
      case "flex.messaging.io.ArrayCollection" =>
        buffer += 0x07.asInstanceOf[Byte]
        writeString(buffer, value.objectType)
        encode(buffer, value.get("array"))
      case _ =>
        writeInt(buffer, (value.size << 4) | 3)
        writeString(buffer, value.objectType)

        val keyOrder = mutable.MutableList[String]()
        import scala.collection.JavaConversions._
        for (key <- value.keySet) {
          writeString(buffer, key)
          keyOrder += key
        }

        for (key <- keyOrder) {
          encode(buffer, value.get(key))
        }

    }
  }

  def randomUID: String = {
    val bytes = new Array[Byte](16)
    val random = new Random(5)
    random.nextBytes(bytes)
    val ret: StringBuilder = new StringBuilder
    for (i <- 0 until bytes.length) {
      if (i == 4 || i == 6 || i == 8 || i == 10)
        ret.append("-")
      ret.append(f"${bytes(i)}%02X")
    }

    ret.toString()
  }
}
