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

package com.itsdamiya.fateclasher.platform.encoding

import com.itsdamiya.fateclasher.{MutableList, UnitSpec}
import scala.collection.mutable
import com.gvaneyck.rtmp.encoding.AMF3Encoder

class EncodingSpec extends UnitSpec with MutableList {

  describe("An encoder") {
    describe("when dealing with integers") {
      val encodeInt = PrivateMethod[Unit]('writeInt)

      it("should encode very low values") {
        AMFEncoder invokePrivate encodeInt(buffer, 1)

        assert(buffer(0) === 1)
      }
      it("should encode low values") {
        AMFEncoder invokePrivate encodeInt(buffer, 160)

        assert(buffer(0) === -127)
        assert(buffer(1) === 32)
      }
      it("should encode high values") {
        AMFEncoder invokePrivate encodeInt(buffer, 18000)

        assert(buffer(0) === -127)
        assert(buffer(1) === -116)
        assert(buffer(2) === 80)
      }
      it("should encode very high values") {
        AMFEncoder invokePrivate encodeInt(buffer, 2500000)

        assert(buffer(0) === -128)
        assert(buffer(1) === -52)
        assert(buffer(2) === -91)
        assert(buffer(3) === -96)
      }
      it("should encode negative values") {
        AMFEncoder invokePrivate encodeInt(buffer, -100)

        assert(buffer(0) === -1)
        assert(buffer(1) === -1)
        assert(buffer(2) === -1)
        assert(buffer(3) === -100)
      }
    }
    describe("when dealing with strings") {
      new AMF3Encoder().testString()
      val encodeString = PrivateMethod[Unit]('writeString)

      it("should encode empty values") {
        AMFEncoder invokePrivate encodeString(buffer, "")
        assert(buffer(0) === 1)
      }
      it("should encode values") {
        AMFEncoder invokePrivate encodeString(buffer, "Hello World!")

        assert(buffer.toArray === Array(25, 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 33))
      }
    }
  }
}
