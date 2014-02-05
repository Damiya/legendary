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

package com.itsdamiya.legendary.cache

import play.api.libs.ws.{WS, WSResponse}
import play.api.mvc.{Content, Request, Results, SimpleResult}
import scala.concurrent.Future
import play.Logger
import play.api.Play.current
import scala.concurrent.duration.Duration
import play.api.libs.json.{JsValue, Json}
import com.itsdamiya.legendary.utils.DefaultWebServices
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.http.DefaultWriteables
import com.itsdamiya.legendary.actions.AuthenticatedRequest
import play.api.mvc._


object CacheableExternalWS extends Results with DefaultWebServices {


  def apply(cacheKey: String, timeToLive: Int, url: String)(resultTransformer: String => JsValue)(responder: WSResponse => SimpleResult): Future[SimpleResult] = {
    val cacheResult = Cache.getAs[JsValue](cacheKey)
    cacheResult match {
      case Some(value) =>
        Logger.debug(s"Cache hit for $cacheKey")
        Future.successful(Ok(value))
      case None =>
        Logger.debug(s"Cache miss for $cacheKey")
        WS.url(url).withDefaultHeaders().get().map { response =>
          Cache.set(cacheKey, resultTransformer(response.body), 0, timeToLive)
          responder(response)
        }
    }
  }

  def apply(cacheKey: String, timeToLive: Duration, url: String)(implicit request: Request[AnyContent]): Future[SimpleResult] = {
    apply(cacheKey, timeToLive.toSeconds.toInt, url)(result => Json.parse(result))(response => Ok(response.json))
  }
}