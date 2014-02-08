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

package com.itsdamiya.fateclasher.loginqueue

import akka.actor.ActorRef
import play.api.libs.json._
import spray.httpx.PlayJsonSupport
import com.gvaneyck.rtmp.ServerInfo

trait LoginQueueProtocol extends PlayJsonSupport {
  implicit val lqTokenFormat = Json.format[LQToken]
  implicit val lqTickerReader = Json.reads[LQTicker]
  implicit val inGameCredentialsReader = Json.reads[InGameCredentials]
  implicit val authenticateResponseReader = Json.reads[AuthenticateResponse]
  implicit val authTokenResponseReader = Json.reads[AuthTokenResponse]
  implicit val tickerResponseReader = Json.reads[TickerResponse]
}

/**
 * A command to retrieve the authToken from the LQ
 *
 * @param targetServer Target League Server
 * @param lqt Most current LQToken
 * @param originalSender ActorRef pointing to the actor that requested a Login Token
 */
case class RetrieveAuthTokenCommand(targetServer: ServerInfo, lqt: LQToken, originalSender: ActorRef)

/**
 * A command to poll the ticker again
 *
 * @param targetServer Target League Server
 * @param lqt Most current LQToken
 * @param rate LQ Rate from original Authenticate call
 * @param delay LQ Delay from original Authenticate call
 * @param champ Ticker name
 * @param originalSender ActorRef pointing to the actor that requested a Login Token
 */
case class CheckTickerCommand(targetServer: ServerInfo, lqt: LQToken, rate: Int, delay: Int, champ: String, originalSender: ActorRef)

// End Internal Commands

// Data Types

/**
 * A single ticker value from the array of tickers in a status==QUEUE LoginQueueResponse
 *
 * @param id Unknown
 * @param node Unknown
 * @param champ Ticker name
 * @param current Unknown
 */
case class LQTicker(id: Int, node: Int, champ: String, current: Int)

/**
 * Unknown use. Maybe represents a queue skip if you're ingame when there's a login queue?
 *
 * @param encryptionKey Unknown
 * @param handshakeToken Unknown
 * @param inGame Unknown
 * @param serverIp Unknown
 * @param serverPort Unknown
 * @param summonerId Unknown
 */
case class InGameCredentials(encryptionKey: Option[String], handshakeToken: Option[String], inGame: Boolean,
                             serverIp: Option[String], serverPort: Option[Int], summonerId: Option[Int])

/**
 * LQ Token class, represent a partial login token from the Riot Games login service.
 *
 * @param account_id As named
 * @param account_name As named
 * @param fingerprint Signed representation of the history of the token
 * @param other Unknown
 * @param resources Authority (Currently only 'lol')
 * @param signature Unknown
 * @param timestamp As named
 * @param uuid Unknown
 */
case class LQToken(account_id: Int, account_name: String, fingerprint: String, other: Option[String],
                   resources: Option[String], signature: String, timestamp: Long, uuid: String)

// End Data Types

// Server Responses

/**
 * Response to an /authenticate call
 *
 * All optional values are only sent down if there's a login queue
 *
 * @param delay Milliseconds until the client should poll again
 * @param inGameCredentials Unknown use
 * @param lqt A signed LQToken
 * @param rate # of players allowed onto the platform per (x)? Unknown X, probably listed in a riot tweet someplace
 * @param reason Unk. seems to be login_rate mostly?
 * @param status LOGIN or QUEUE
 * @param user Username sent in
 * @param tickers Array of Tickers
 * @param node Unknown
 * @param vcap Maximum number of players to show for the queue depth (20k always?)
 * @param backlog # of players ahead of you in the queue
 * @param champ Ticker name to check
 */
case class AuthenticateResponse(delay: Int, inGameCredentials: InGameCredentials, lqt: LQToken,
                                rate: Int, reason: String, status: String, user: String, tickers: Option[Array[LQTicker]], node: Option[Int],
                                vcap: Option[Int], backlog: Option[Int], champ: Option[String])

/**
 * Response to a /ticker/{champName} call
 *
 * I don't have good data on what each of the numbers represents and they can change.
 * Looks like { backlog: a, 1004: b, 1005: c, 1006: d, 1007: e, 1008: f)
 *
 * @param backlog Unknown
 * @param numOne Unknown
 * @param numTwo Unknown
 * @param numThree Unknown
 * @param numFour Unknown
 * @param numFive Unknown
 */

case class TickerResponse(backlog: Int, numOne: Int, numTwo: Int, numThree: Int, numFour: Int, numFive: Int)

/**
 * Response to a /token call, generating a signed LQ Token we can use to log into the League of Legends platform
 * @param lqt Fully signed LQToken including resource allowance and an 'other'
 * @param status LOGIN (possibly QUEUE if called too early? TBD)
 */
case class AuthTokenResponse(lqt: LQToken, status: String)
