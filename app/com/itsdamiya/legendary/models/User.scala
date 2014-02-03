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

package com.itsdamiya.legendary.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import scala.slick.driver.PostgresDriver.simple._
import com.itsdamiya.legendary.utils.BCryptPasswordHasher

case class User(id: Option[Long] = None, username: String, firstName: String,
    lastName: String, email: String,
    password: String) {
}

object User extends ((Option[Long], String, String, String, String, String) => User) {
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

case class UserPass(username: String, password: String)

object UserPass extends ((String, String) => UserPass) {
  implicit val userPassReads = Json.reads[UserPass]
}

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def username = column[String]("username")

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  def email = column[String]("email")

  def password = column[String]("password")

  def * = (id.?, username, firstName, lastName, email, password) <> (User.tupled, User.unapply)
}

object Users extends DAO {
  def count(implicit s: Session): Int = Query(Users.length).first

  def insert(user: User)(implicit s: Session) = {
    val hashedPassword = BCryptPasswordHasher.hash(user.password)

    Users.insert(user.copy(password = hashedPassword))
  }

  def findUserByName(username: String)(implicit s: Session): Option[User] = {
    Users.where(_.username === username).firstOption
  }

  def findUserByEmail(email: String)(implicit s: Session): Option[User] = {
    Users.where(_.email === email).firstOption
  }
}

