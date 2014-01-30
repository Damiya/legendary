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

package models

import play.api.libs.json._
import org.joda.time.DateTime
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime


trait ModelConverters {
  implicit val authTokenWrites: Writes[AuthToken] = (
    (__ \ 'token).write[String] and
      (__ \ 'expires).write[DateTime]
    ) {
    (a: AuthToken) => (a.token, a.expirationTime)
  }

  implicit val userPassReads = Json.reads[UserPass]

  implicit val userWrites: Writes[User] = (
    (__ \ "id").writeNullable[Long] and
      (__ \ "username").write[String] and
      (__ \ "firstName").write[String] and
      (__ \ "lastName").write[String] and
      (__ \ "email").write[String]
    ) {
    (u: User) => (u.id, u.username, u.firstName, u.lastName, u.email)
  }

  implicit val userReads: Reads[User] = (
    (__ \ 'username).read[String] and
      (__ \ 'firstName).read[String] and
      (__ \ 'lastName).read[String] and
      (__ \ 'email).read[String](email) and
      (__ \ 'password).read[String]
    ) {
    (username: String, firstName: String, lastName: String, email: String, password: String) =>
      User(None, username, firstName, lastName, email, password)
  }
}
