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

package com.itsdamiya.fateclasher.commands

import com.gvaneyck.rtmp.ServerInfo

/**
 * Used to perform parts of the login sequence that rely on having a username and password (Supervisor, Login Queue)
 * @param username League username
 * @param password League password
 */
case class LoginWithCredentials(username: String, password: String)

case class OtherCommand()
