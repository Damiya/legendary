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

package models


import scala.slick.driver.PostgresDriver.simple._
import play.Logger
import play.api.db._
import play.api.libs.json.{JsValue, Json}

case class User(id: Option[Long] = None, username: String, firstName: String,
                lastName: String, email: String,
                password: String)

class Users(tag: Tag) extends Table[User](tag, "users") {

  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.NotNull)

  def username = column[String]("username")

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  def email = column[String]("email")

  def password = column[String]("password")

  def * = (id, username, firstName, lastName, email, password) <>(User.tupled, User.unapply)
}

trait UserComponent {

  import play.api.Play.current

  val Users = TableQuery[Users]

  private val autoInc = Users returning Users.map(_.id) into {
    case (p, pid) => p.copy(id = pid)
  }

  def insert(user: User)(implicit session: Session): User = {
    autoInc.insert(user)
  }

  def findUserByName(username: String): Option[User] = {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        Users.where(_.username === username).firstOption
    }
  }

  def findUserByEmail(email: String): Option[User] = {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        Users.where(_.email === email).firstOption
    }
  }

  def saveNewUser(user: User): Option[User] = {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        val existingUser = Users.where(_.username === user.username).firstOption
        if (existingUser.isDefined) {
          None
        } else {
          Some(insert(user))
        }

    }
  }
}

object UserDAO extends UserComponent
