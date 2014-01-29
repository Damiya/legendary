/*
 * Copyright 2014 Kate von Roeder (katevonroder at gmail dot com) - twitter: @itsdamiya
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

package controllers


import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.Logger
import models.AuthTokenParser
import security.{BCryptPasswordHasher, UserService}

object AuthController extends Controller with AuthTokenParser {
  def authenticateAjax() = Action(parse.json) {
    implicit request =>

      val username = (request.body \ "username").asOpt[String]
      val password = (request.body \ "password").asOpt[String]
      UserService.find(username.getOrElse(null)).map {
        user =>
          if (BCryptPasswordHasher.matches(user.password, password.getOrElse(null))) {
            val authToken = UserService.getAuthToken(user)
            Logger.debug("Got a token. Submitting it back")
            Ok(Json.toJson(authToken))
          } else {
            Logger.debug(s"Invalid login attempt: $username $password")
            Unauthorized("Invalid credentials submitted.")
          }
      } getOrElse Unauthorized("Invalid credentials submitted.")
  }

}
