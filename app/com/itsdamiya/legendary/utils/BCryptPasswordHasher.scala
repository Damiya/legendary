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

package com.itsdamiya.legendary.utils

import org.mindrot.jbcrypt.BCrypt

object BCryptPasswordHasher {
  val DefaultRounds = 10
  val RoundsProperty = "bcrypt.rounds"

  val id = "hasher"

  /**
   * Hashes a password. This implementation does not return the salt because it is not needed
   * to verify passwords later.  Other implementations might need to return it so it gets saved in the
   * backing store.
   *
   * @param plainPassword the password to hash
   * @return a PasswordInfo containing the hashed password.
   */
  def hash(plainPassword: String): String = {
    val logRounds = DefaultRounds
    BCrypt.hashpw(plainPassword, BCrypt.gensalt(logRounds))
  }

  /**
   * Checks if a password matches the hashed version
   *
   * @param hashedPass the password retrieved from the backing store (by means of UserService)
   * @param suppliedPassword the password supplied by the user trying to log in
   * @return true if the password matches, false otherwise.
   */
  def matches(hashedPass: String, suppliedPassword: String): Boolean = {
    BCrypt.checkpw(suppliedPassword, hashedPass)
  }
}