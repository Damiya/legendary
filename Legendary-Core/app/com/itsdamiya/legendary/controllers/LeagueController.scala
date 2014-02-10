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

package com.itsdamiya.legendary.controllers

import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.duration._
import com.itsdamiya.legendary.utils.{MagicStrings, DefaultWebServices}
import play.api.libs.json.{JsValue, Json}
import com.itsdamiya.legendary.models.UserPass
import com.itsdamiya.legendary.actions.Secured
import com.itsdamiya.legendary.cache.CacheableExternalWS

object LeagueController extends Controller with DefaultWebServices {

  def login(): Action[JsValue] = Secured(parse.json) { request =>
    Ok(Json.obj(
      "result" -> "Ok"
    ))
  }

  def featuredGames(): Action[AnyContent] = Secured.async { request =>
    CacheableExternalWS("featuredGames", 5.minutes, MagicStrings.featuredGamesUrl)
  }

  def landingPage(): Action[AnyContent] = Secured.async { request =>
    CacheableExternalWS("landingPage", 5.hours, MagicStrings.landingPageUrl)
  }

  def logout(): Action[JsValue] = Secured(parse.json) { request =>
    Ok(Json.obj(
      "result" -> "Logged Out"
    ))
  }
}
