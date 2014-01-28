package models

import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import scala.slick.driver.PostgresDriver.simple._

import play.api.db._
import securesocial.core.providers.Token
import play.Logger

case class LegendaryToken(pid: Option[Long] = None, uuid: String, email: String,
                          creationTime: DateTime, expirationTime: DateTime, isSignUp: Boolean) {
  def isExpired = expirationTime.isBeforeNow
}

trait LegendaryTokenComponent {

  import play.api.Play.current

  class LegendaryTokens(tag: Tag) extends Table[LegendaryToken](tag, "legendaryTokens") {
    def pid = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.NotNull)

    def uuid = column[String]("uuid")

    def email = column[String]("email")

    def creationTime = column[DateTime]("creationTime")

    def expirationTime = column[DateTime]("expirationTime")

    def isSignUp = column[Boolean]("isnSignUp")

    def * = (pid, uuid, email, creationTime, expirationTime, isSignUp) <>(LegendaryToken.tupled, LegendaryToken.unapply)
  }

  val LegendaryTokens = TableQuery[LegendaryTokens]

  private val legendaryTokensAutoInc = LegendaryTokens returning LegendaryTokens.map(_.pid) into {
    case (p, pid) => p.copy(pid = pid)
  }

  def insert(token: LegendaryToken)(implicit session: Session): LegendaryToken = {
    legendaryTokensAutoInc.insert(token)
  }

  def deleteExpiredTokens() {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        LegendaryTokens
    }
  }

  def deleteAllTokens() {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        LegendaryTokens.drop(0).delete
    }

  }

  def deleteToken(s: String) {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        Logger.debug("Deleting token " + s)
        LegendaryTokens.where(_.uuid === s).delete
    }
  }

  def findToken(s: String): Option[Token] = {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        val token = LegendaryTokens.where(_.uuid === s).firstOption

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
        insert(LegendaryToken(None, token.uuid, token.email, token.creationTime, token.expirationTime, token.isSignUp))
    }
  }

}

object LegendaryTokenDAO extends LegendaryTokenComponent