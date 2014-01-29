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


import play.api.libs.json._

import com.github.tototoshi.slick.PostgresJodaSupport._
import scala.slick.driver.PostgresDriver.simple._
import java.util.UUID
import play.api.db._
import play.Logger
import org.joda.time.DateTime

case class AuthToken(id: Option[Long], token: String, creationTime: DateTime, expirationTime: DateTime, userId: Option[Long])

trait AuthTokenComponent {

  import play.api.Play.current

  class AuthTokens(tag: Tag) extends Table[AuthToken](tag, "auth_tokens") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.NotNull)

    def token = column[String]("token")

    def creationTime = column[DateTime]("creationTime")

    def expirationTime = column[DateTime]("expirationTime")

    def userId = column[Option[Long]]("userId", O.NotNull)

    def legendaryUser = foreignKey("legendaryUser_FK", userId, UserDAO.Users)(_.id)

    def * = (id, token, creationTime, expirationTime, userId) <>(AuthToken.tupled, AuthToken.unapply)
  }

  val AuthTokens = TableQuery[AuthTokens]

  private val autoInc = AuthTokens returning AuthTokens.map(_.id) into {
    case (p, pid) => p.copy(id = pid)
  }

  private def insert(token: AuthToken)(implicit session: Session): AuthToken = {
    autoInc.insert(token)
  }

  def findOrCreateAuthToken(user: User): AuthToken = {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        val query = AuthTokens.where(_.userId === user.id).firstOption
        query.getOrElse(insert(AuthToken(None, UUID.randomUUID().toString, DateTime.now(), DateTime.now(), user.id)))
    }
  }
}

object AuthTokenDAO extends AuthTokenComponent