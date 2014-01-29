package models

import org.joda.time.{LocalDateTime, DateTime}
import com.github.tototoshi.slick.PostgresJodaSupport._
import scala.slick.driver.PostgresDriver.simple._

import play.api.db._
import securesocial.core.providers.Token
import play.Logger

trait TokenComponent {

  import play.api.Play.current

  class Tokens(tag: Tag) extends Table[Token](tag, "tokens") {
    def uuid = column[String]("uuid", O.PrimaryKey)

    def email = column[String]("email")

    def creationTime = column[DateTime]("creationTime")

    def expirationTime = column[DateTime]("expirationTime")

    def isSignUp = column[Boolean]("isSignUp")

    def * = (uuid, email, creationTime, expirationTime, isSignUp) <>(Token.tupled, Token.unapply)
  }

  val Tokens = TableQuery[Tokens]

  def deleteExpiredTokens() {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        val query = for {
          token <- Tokens
          if token.expirationTime < DateTime.now()
        } yield token
        query.delete
    }
  }

  def deleteAllTokens() {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        Tokens.drop(0).delete
    }

  }

  def deleteToken(s: String) {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        Tokens.where(_.uuid === s).delete
    }
  }

  def findToken(s: String): Option[Token] = {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        val token = Tokens.where(_.uuid === s).firstOption

        if (token.isDefined) {
          val someToken = token.get
          Some(Token(someToken.uuid, someToken.email, someToken.creationTime, someToken.expirationTime, someToken.isSignUp))
        } else {
          None
        }

    }
  }

  def saveNewToken(token: Token) {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        Tokens += Token(token.uuid, token.email, token.creationTime, token.expirationTime, token.isSignUp)
    }
  }
}

object TokenDAO extends TokenComponent