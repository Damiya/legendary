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

package com.itsdamiya.fateclasher

import com.gvaneyck.rtmp.ServerInfo
import com.itsdamiya.fateclasher.loginqueue.LQToken

package object commands {

  /**
   * Used to perform parts of the login sequence that rely on having a username and password (Supervisor, Login Queue)
   * @param username League username
   * @param password League password
   * @param targetServer Destination server
   */
  case class LoginWithCredentials(username: String, password: String, targetServer: ServerInfo)

  /**
   * Used for the part of the login sequence based on a login queue token (Platform login)
   * @param lqt Signed login queue token
   * @param targetServer Destination server
   */
  case class LoginWithToken(lqt: LQToken, targetServer: ServerInfo)

}
