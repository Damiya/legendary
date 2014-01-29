package services

import play.api.Application
import securesocial.core._
import securesocial.core.IdentityId
import securesocial.core.providers.Token
import models.{UserDAO, TokenDAO}


class UserService(application: Application) extends UserServicePlugin(application)  {
  def find(id: IdentityId): Option[Identity] = {
    UserDAO.findUserByIdentityId(id)
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    UserDAO.findUserByEmailAndProvider(email, providerId)
  }

  def save(user: Identity): Identity = {
    UserDAO.saveNewUser(user)
  }

  def save(token: Token) {
    TokenDAO.saveNewToken(token)
  }

  def findToken(token: String): Option[Token] = {
    TokenDAO.findToken(token)
  }

  def deleteToken(token: String) {
    TokenDAO.deleteToken(token)
  }

  def deleteTokens() {
    TokenDAO.deleteAllTokens()
  }

  def deleteExpiredTokens() {
    TokenDAO.deleteExpiredTokens()
  }
}