package services

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.IdentityId
import securesocial.core.providers.Token
import models.{LegendaryUserDAO, LegendaryTokenDAO}


class UserService(application: Application) extends UserServicePlugin(application)  {
  private var tokens = Map[String, Token]()

  def find(id: IdentityId): Option[Identity] = {
    LegendaryUserDAO.findUserByIdentityId(id)
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    LegendaryUserDAO.findUserByEmailAndProvider(email, providerId)
  }

  def save(user: Identity): Identity = {
    LegendaryUserDAO.saveNewUser(user)
  }

  def save(token: Token) {
    LegendaryTokenDAO.saveNewToken(token)
  }

  def findToken(token: String): Option[Token] = {
    LegendaryTokenDAO.findToken(token)
  }

  def deleteToken(token: String) {
    LegendaryTokenDAO.deleteToken(token)
  }

  def deleteTokens() {
    LegendaryTokenDAO.deleteAllTokens()
  }

  def deleteExpiredTokens() {
    LegendaryTokenDAO.deleteExpiredTokens()
  }
}