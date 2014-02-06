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
import play.api.mvc.{RequestHeader, EssentialFilter, EssentialAction}
import play.api.Play.current

object HTTPSFilter extends EssentialFilter {
  implicit lazy val httpsPort: Int = current.configuration.getInt("https.port").getOrElse(443)

  def apply(next: EssentialAction): EssentialAction = new HTTPSAction(next)
}

class HTTPSAction(next: EssentialAction)(implicit httpsPort: Int) extends EssentialAction {
  def apply(request: RequestHeader) = {
    def continue = next(request)
    val xForwardedProto = request.headers.get("X-Forwarded-Proto").getOrElse("None")

    if (request.secure || xForwardedProto  == "https") {
      continue
    } else {
      Done(Redirect("https://" + request.host.split(":")(0) + s":$httpsPort" + request.uri))
    }
  }
}
