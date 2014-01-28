package services

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.IdentityId
import securesocial.core.providers.Token
import models.{ModelImplicits, DAO}


class UserService(application: Application) extends UserServicePlugin(application) with ModelImplicits {
  private var users = Map[String, Identity]()
  private var tokens = Map[String, Token]()

  def find(id: IdentityId): Option[Identity] = {
    DAO.findUserByIdentityId(id)
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    DAO.findUserByEmailAndProvider(email, providerId)
  }

  def save(user: Identity): Identity = {
    DAO.saveNewUser(user)
  }

  def save(token: Token) {
    tokens += (token.uuid -> token)
  }

  def findToken(token: String): Option[Token] = {
    tokens.get(token)
  }

  def deleteToken(uuid: String) {
    tokens -= uuid
  }

  def deleteTokens() {
    tokens = Map()
  }

  def deleteExpiredTokens() {
    tokens = tokens.filter(!_._2.isExpired)
  }
}