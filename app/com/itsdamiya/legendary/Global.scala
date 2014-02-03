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

package com.itsdamiya.legendary

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

import play.api._
import play.api.db.slick._
import play.api.Play.current
import com.itsdamiya.legendary.models.{ User, Users }

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    InitialData.insert()
  }
}

object InitialData {
  def insert() {
    DB.withSession { implicit s: Session =>
      if (Users.count == 0 && play.api.Play.isDev) {
        Users.insert(User(Option(1L), "jerleminara", "Kate", "von Roeder", "katevonroeder@gmail.com", "tempdev"))
      }
    }
  }
}