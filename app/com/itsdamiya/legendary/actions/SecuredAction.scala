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

package com.itsdamiya.legendary.actions

import play.api.mvc._
import scala.concurrent.Future.{ successful => resolve }
import com.itsdamiya.legendary.models.{ UserSession, User }
import scala.concurrent.Future
import com.itsdamiya.legendary.cache.Cache
import play.api.Play.current

case class AuthenticatedRequest[A](userSession: UserSession, request: Request[A]) extends WrappedRequest[A](request)

object SecuredAction extends ActionBuilder[AuthenticatedRequest] with Results {
  def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
    request.headers.get("X-Auth-Token") match {
      case Some(authToken) =>
        Cache.getAs[UserSession](authToken) match {
          case Some(userSession) =>
            block(AuthenticatedRequest(userSession, request))
          case None =>
            resolve(Forbidden("You must log in to access that resource."))
        }

      case None =>
        resolve(Forbidden("You must log in to access that resource."))
    }

  }
}