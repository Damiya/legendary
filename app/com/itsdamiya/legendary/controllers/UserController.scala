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

import play.api.mvc.Controller
import play.api.db.slick._
import play.api.Play.current
import play.api.Play
import play.api.libs.json.Json
import com.itsdamiya.legendary.models.{Users, User}
import com.itsdamiya.legendary.utils.BCryptPasswordHasher

object UserController extends Controller {
  def create() = DBAction(parse.json) { implicit rs =>
    if (Play.isDev) {
      rs.request.body.validate[User].map { user =>
        val hashedPassword = BCryptPasswordHasher.hash(user.password)

        val newUser = user.copy(password = hashedPassword)

        Users.insert(newUser)
        Ok(Json.toJson("Success"))
      }.getOrElse {
        BadRequest("Invalid user registration")
      }
    } else {
      BadRequest("Registration doesn't work in production")
    }
  }
}
