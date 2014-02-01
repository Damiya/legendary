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

import play.api.db._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import scala.Some
import scala.slick.driver.PostgresDriver.simple._


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

trait UserComponent {

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.NotNull)

    def username = column[String]("username")

    def firstName = column[String]("first_name")

    def lastName = column[String]("last_name")

    def email = column[String]("email")

    def password = column[String]("password")

    def * = (id, username, firstName, lastName, email, password) <>(User.tupled, User.unapply)
  }

  import play.api.Play.current

  val Users = TableQuery[Users]

  private val autoInc = Users returning Users.map(_.id) into {
    case (p, pid) => p.copy(id = pid)
  }

  def insert(user: User)(implicit session: Session): User = {
    autoInc.insert(user)
  }

  def findUserByName(username: String): Option[User] = {
    Database.forDataSource(DB.getDataSource()).withSession { implicit session =>
      Users.where(_.username === username).firstOption
    }
  }

  def findUserByEmail(email: String): Option[User] = {
    Database.forDataSource(DB.getDataSource()).withSession { implicit session =>
      Users.where(_.email === email).firstOption
    }
  }

  def saveNewUser(user: User): Option[User] = {
    Database.forDataSource(DB.getDataSource()).withSession { implicit session =>
      Users.where(_.username === user.username).firstOption match {
        case Some(existingUser) => // User already exists
          None
        case None =>
          Some(insert(user))

      }
    }
  }

  def findUserByToken(token: String): Option[User] = {
    Database.forDataSource(DB.getDataSource()).withSession { implicit session =>
      val userForToken = for {
        t <- AuthTokenDAO.AuthTokens if t.token === token
        u <- Users if t.userId === u.id
      } yield u

      userForToken.firstOption
    }
  }
}

object UserDAO extends UserComponent {

}
