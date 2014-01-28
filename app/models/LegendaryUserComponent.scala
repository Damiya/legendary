package models

import securesocial.core._
import securesocial.core.OAuth2Info
import securesocial.core.OAuth1Info
import securesocial.core.IdentityId
import securesocial.core.PasswordInfo
import scala.slick.driver.PostgresDriver.simple._

import play.api.db._
import org.joda.time.DateTime

case class LegendaryUser(pid: Option[Long] = None, identityId: IdentityId, firstName: String,
                         lastName: String, fullName: String, email: Option[String],
                         avatarUrl: Option[String], authMethod: AuthenticationMethod,
                         oAuth1Info: Option[OAuth1Info] = None,
                         oAuth2Info: Option[OAuth2Info] = None,
                         passwordInfo: Option[PasswordInfo] = None) extends Identity

case class Token(uuid: String, email: String, creationTime: DateTime, expirationTime: DateTime, isSignUp: Boolean) {
  def isExpired = expirationTime.isBeforeNow
}

trait LegendaryUserComponent extends ModelImplicits {

  import play.api.Play.current


  class LegendaryUsers(tag: Tag) extends Table[LegendaryUser](tag, "legendaryUsers") {

    def pid = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.NotNull)

    def identityId = column[IdentityId]("identityId")

    def firstName = column[String]("firstName")

    def lastName = column[String]("lastName")

    def fullName = column[String]("fullName")

    def email = column[Option[String]]("email")

    def avatarUrl = column[Option[String]]("avatarUrl")

    def authMethod = column[AuthenticationMethod]("authMethod")

    def oauth1Info = column[Option[OAuth1Info]]("oauthInfo1")

    def oauth2Info = column[Option[OAuth2Info]]("oauthInfo2")

    def passwordInfo = column[Option[PasswordInfo]]("passwordInfo")

    def * = (pid, identityId, firstName, lastName, fullName, email, avatarUrl, authMethod, oauth1Info, oauth2Info, passwordInfo) <>(LegendaryUser.tupled, LegendaryUser.unapply)
  }

  val LegendaryUsers = TableQuery[LegendaryUsers]

  private val legendaryUsersAutoInc = LegendaryUsers returning LegendaryUsers.map(_.pid) into {
    case (p, pid) => p.copy(pid = pid)
  }

  def insert(user: LegendaryUser)(implicit session: Session): LegendaryUser = {
    legendaryUsersAutoInc.insert(user)
  }

  private def convertUserToIdentity(user: LegendaryUser): Identity = {
    SocialUser(user.identityId, user.firstName, user.lastName, user.fullName, user.email, user.avatarUrl, user.authMethod,
      user.oAuth1Info, user.oAuth2Info, user.passwordInfo).asInstanceOf[Identity]
  }

  def findUserByIdentityId(id: IdentityId): Option[Identity] = {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        Some(LegendaryUsers.where(_.identityId === id).first())
    }
  }

  def findUserByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        val result = LegendaryUsers.where(_.email === email).first()
        if (result.identityId.providerId == providerId) {
          Some(result)
        } else {
          None
        }
    }
  }

  def saveNewUser(user: Identity): Identity = {
    val newUser = LegendaryUser(None, user.identityId, user.firstName, user.lastName, user.fullName, user.email, user.avatarUrl, user.authMethod, user.oAuth1Info, user.oAuth2Info, user.passwordInfo)
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        insert(newUser)
    }

    newUser

  }

}


