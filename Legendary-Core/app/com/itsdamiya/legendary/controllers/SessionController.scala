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

import play.api.mvc.{ Action, Controller }
import play.api.Play.current
import com.itsdamiya.legendary.utils.BCryptPasswordHasher
import play.Logger
import play.api.db.slick._
import java.util.UUID
import scala.concurrent.duration._
import com.itsdamiya.legendary.cache.Cache
import com.itsdamiya.legendary.models.{ UserSession, Users, UserPass }
import play.api.libs.json.{ JsValue, Json }
import com.itsdamiya.legendary.actions.Secured

object SessionController extends Controller {

  def create(): Action[JsValue] = DBAction(parse.json) { implicit rs =>
    rs.request.body.validate[UserPass].asOpt match {
      case Some(userPass) =>
        Users.findUserByName(userPass.username.toLowerCase) match {
          case Some(user) =>
            if (BCryptPasswordHasher.matches(user.password, userPass.password)) {
              Logger.info("Issued a new token to " + user.username)
              val authToken = UUID.randomUUID().toString
              val userSession = new UserSession(user, authToken)
              Cache.set(authToken, userSession, 2.hours)
              Ok(Json.obj(
                "value" -> authToken
              ))
            } else {
              Unauthorized("Invalid credentials submitted.")
            }
          case None => Unauthorized("Invalid credentials submitted.")
        }
      case None => BadRequest("Malformed credentials submitted.")
    }
  }

  def destroy(): Action[JsValue] = Secured(parse.json) { request =>
    Cache.remove(request.userSession.authToken)
    Logger.info(s"${request.userSession.user.username} logged out.")
    Ok(Json.obj(
      "result" -> "Session destroyed"
    ))
  }
}
