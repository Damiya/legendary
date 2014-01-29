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

package security

import play.api.Application
import models.{AuthTokenDAO, AuthToken, User, UserDAO}


object UserService {
  def find(username: String): Option[User] = {
    if (username == null) {
      return None
    }

    UserDAO.findUserByName(username)
  }

  def findByEmail(email: String): Option[User] = {
    UserDAO.findUserByEmail(email)
  }

  def save(user: User): User = {
    UserDAO.saveNewUser(user)
  }

  def getAuthToken(user: User): AuthToken = {
    AuthTokenDAO.findOrCreateAuthToken(user)
  }

}