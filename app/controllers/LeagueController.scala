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

package controllers

import actions.SecuredAction
import actors.{ConnectionStatus, LeagueClientImpl, LeagueClient}
import akka.actor._
import akka.pattern.AskableActorSelection
import akka.util.Timeout
import models.UserPass
import play.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import scala.Some
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import play.api.libs.ws._
import utils.{DefaultWebServices, MagicStrings}

object LeagueController extends Controller with DefaultWebServices {

  private def getLeagueClient(username: String): LeagueClient = {
    implicit val timeout = Timeout(5.seconds)
    val potentialActor = Akka.system.actorSelection(s"/user/$username")
    val identifyFuture = new AskableActorSelection(potentialActor).ask(Identify(1))
    val usernameActorRef = Await.result(identifyFuture, 5.seconds).asInstanceOf[ActorIdentity].getRef
    if (usernameActorRef == null) {
      TypedActor(Akka.system).typedActorOf(TypedProps[LeagueClientImpl]().withTimeout(60.seconds), name = username)
    } else {
      TypedActor(Akka.system).typedActorOf(TypedProps[LeagueClientImpl]().withTimeout(60.seconds), usernameActorRef)
    }
  }


  def login() = SecuredAction.async(parse.json) { authenticatedRequest =>
    val loginActor = getLeagueClient(authenticatedRequest.user.username)

    authenticatedRequest.request.body.validate[UserPass].asOpt match {
      case Some(user) =>
        loginActor.login(user).map { result =>
          val resultObj = Json.obj(
            "result" -> result
          )
          Ok(resultObj)
        }
      case None =>
        Future.successful(Ok("Ok"))
    }
  }

  def featuredGames() = SecuredAction.async { authenticatedRequest =>
    WS.url(MagicStrings.featuredGamesUrl)
      .withDefaultHeaders().get().map { response =>
      Ok(response.json)
    }
  }

  def landingPage() = SecuredAction.async { authenticatedRequest =>
    WS.url(MagicStrings.landingPageUrl)
      .withDefaultHeaders().get().map { response =>
      Ok(response.json)
    }
  }

  def logout() = SecuredAction { authenticatedRequest =>
    val loginActor = getLeagueClient(authenticatedRequest.user.username)
    if (loginActor.isConnected) {
      loginActor.logout()
      Ok(Json.obj(
        "result" -> ConnectionStatus.LOGGED_OUT
      ))
    } else {
      Logger.error("Attempted to log out despite not being logged in on the client.")
      BadRequest("Not current logged in.")
    }
  }
}
