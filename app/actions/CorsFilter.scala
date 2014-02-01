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

package actions

import controllers.Default
import play.api.Logger
import play.api.mvc.{SimpleResult, RequestHeader, Filter}

case class CORSFilter() extends Filter {

  import scala.concurrent._
  import ExecutionContext.Implicits.global

  // Todo: This code is ugly. needs to be deugly'd

  val defaultMethods = "GET POST DELETE PUT"
  val defaultHeaders = "Origin, X-Requested-With, Content-Type, Accept, X-Auth-Token"

  def isPreFlight(r: RequestHeader) = (
    r.method.toLowerCase.equals("options")
      &&
      r.headers.get("Access-Control-Request-Method").nonEmpty
    )

  def apply(f: (RequestHeader) => Future[SimpleResult])(request: RequestHeader): Future[SimpleResult] = {
    Logger.trace("[cors] filtering request to add cors")
    if (isPreFlight(request)) {
      Logger.trace("[cors] request is preflight")
      Future.successful(Default.Ok.withHeaders(
        "Access-Control-Allow-Origin" -> "http://localhost:*",
        "Access-Control-Allow-Origin" -> "http://damiya.github.com/",
        "Access-Control-Allow-Methods" -> request.headers.get("Access-Control-Request-Method").getOrElse(defaultMethods),
        "Access-Control-Allow-Headers" -> request.headers.get("Access-Control-Request-Headers").getOrElse(defaultHeaders),
        "Access-Control-Allow-Credentials" -> "true"
      ))
    } else {
      Logger.trace("[cors] request is normal")
      f(request).map {
        _.withHeaders(
          "Access-Control-Allow-Origin" -> "http://localhost:*",
          "Access-Control-Allow-Origin" -> "http://damiya.github.com/",
          "Access-Control-Allow-Methods" -> request.headers.get("Access-Control-Request-Method").getOrElse(defaultMethods),
          "Access-Control-Allow-Headers" -> request.headers.get("Access-Control-Request-Headers").getOrElse(defaultHeaders),
          "Access-Control-Allow-Credentials" -> "true"
        )
      }
    }
  }
}