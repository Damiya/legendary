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

import models.{ Users, User }
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.Future.{ successful => resolve }
import play.api.Play.current
import play.api.db.slick._

case class AuthenticatedRequest[A](user: User, request: Request[A]) extends WrappedRequest[A](request)
object SecuredAction
//
//object SecuredAction extends ActionBuilder[AuthenticatedRequest] with Results {
//  def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
//    request.headers.get("X-Auth-Token") match {
//      case Some(authToken) =>
//        Users.findUserByToken(authToken) match {
//          case Some(user) =>
//            block(AuthenticatedRequest(user, request))
//          case None =>
//            resolve(Forbidden("You must log in to access that resource."))
//        }
//
//      case None =>
//        resolve(Forbidden("You must log in to access that resource."))
//    }
//
//  }
//}