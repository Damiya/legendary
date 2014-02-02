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

package controllers

import actions.SecuredAction
import models.{ AuthTokenDAO, UserPass }
import play.Logger
import play.api.libs.json.Json
import play.api.mvc.{ Action, Controller }
import services.UserService
import utils.BCryptPasswordHasher

object TokenController extends Controller {

  def create() = Action(parse.json) { implicit request =>
    request.body.validate[UserPass].asOpt match {
      case Some(userPass) =>
        UserService.find(userPass.username.toLowerCase) match {
          case Some(user) =>
            if (BCryptPasswordHasher.matches(user.password, userPass.password)) {
              Logger.info("Issued a new token to " + user.username)
              Ok(Json.toJson(UserService.getAuthToken(user)))
            } else {
              Unauthorized("Invalid credentials submitted.")
            }
          case None => Unauthorized("Invalid credentials submitted.")
        }
      case None => BadRequest("Malformed credentials submitted.")
    }
  }

  def destroy() = SecuredAction { authenticatedRequest =>
    if (AuthTokenDAO.deleteAuthToken(authenticatedRequest.user)) {
      Logger.info(s"Deleted token for ${authenticatedRequest.user.username}")
      Ok("")
    } else {
      BadRequest("No token found. This is a problem.")
    }
  }
}
