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

package com.itsdamiya.legendary.filters

import play.api.libs.iteratee._
import play.api.mvc.Results._
import play.api.mvc.{ SimpleResult, RequestHeader, EssentialFilter, EssentialAction }
import play.api.Play.current

object HTTPSFilter extends EssentialFilter {
  // scalastyle:off magic.number
  lazy val defaultPort: Int = {
    if (play.api.Play.isProd) {
      443
    } else {
      9443
    }
  }
  // scalastyle:on magic.number

  lazy val httpsPort: Int = current.configuration.getInt("https.port").getOrElse(defaultPort)

  def apply(next: EssentialAction): EssentialAction = new HTTPSAction(next, httpsPort)
}

class HTTPSAction(next: EssentialAction, httpsPort: Int) extends EssentialAction {
  def apply(request: RequestHeader): Iteratee[Array[Byte], SimpleResult] = {
    def continue: Iteratee[Array[Byte], SimpleResult] = next(request)
    val xForwardedProto = request.headers.get("X-Forwarded-Proto").getOrElse("None")

    if (request.secure || xForwardedProto == "https") {
      continue
    } else {
      Done(Redirect("https://" + request.host.split(":")(0) + s":$httpsPort" + request.uri))
    }
  }
}
