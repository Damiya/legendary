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

package com.itsdamiya.fateclasher.utils

import spray.http.{ HttpResponse, HttpRequest, ContentTypes, HttpEntity }
import akka.actor.ActorLogging

trait HTTPTransformers {
  this: ActorLogging =>
  type RequestTransformer = HttpRequest => HttpRequest
  type ResponseTransformer = HttpResponse => HttpResponse

  val logRequest: RequestTransformer = {
    request =>
      log.debug(request.toString)
      request
  }

  val logResponse: ResponseTransformer = {
    response =>
      log.debug(response.toString)
      response
  }

  /**
   * Converts an HTTPResponse into type Application/Json; useful in situations where
   */
  val mapToJson: ResponseTransformer = {
    response =>
      response.withEntity(HttpEntity(ContentTypes.`application/json`, response.entity.data))
  }
}
