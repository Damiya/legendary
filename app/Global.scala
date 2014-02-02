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

import play.api.mvc.WithFilters
import scala.slick.driver.PostgresDriver.simple._
import play.filters.csrf.CSRFFilter
import play.api.db._
import play.api._
import models.{ AuthTokenDAO, UserDAO }

import scala.slick.jdbc.meta.MTable

object Global extends WithFilters(new CSRFFilter()) with GlobalSettings {

  import play.api.Play.current

  override def onStart(app: Application) {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session: Session =>
        if (MTable.getTables("users").list().isEmpty) {
          UserDAO.Users.ddl.create
        }

        if (MTable.getTables("auth_tokens").list().isEmpty) {
          AuthTokenDAO.AuthTokens.ddl.create
        }
    }
  }
}