package models

import securesocial.core._
import scala.slick.driver.PostgresDriver.simple._
import play.Logger
import play.api.db._
import play.api.libs.json.{JsValue, Json}

case class LegendaryUser(pid: Option[Long] = None, identityId: IdentityId, firstName: String,
                         lastName: String, fullName: String, email: Option[String],
                         avatarUrl: Option[String], authMethod: AuthenticationMethod,
                         oAuth1Info: Option[OAuth1Info] = None,
                         oAuth2Info: Option[OAuth2Info] = None,
                         passwordInfo: Option[PasswordInfo] = None) extends Identity


trait LegendaryUserModelImplicits {
  implicit def identityMapper = MappedColumnType.base[IdentityId, String](
  {
    i: IdentityId =>
      val jsonified = Json.obj(
        "userId" -> i.userId,
        "providerId" -> i.providerId
      )
      Json.stringify(jsonified)
  }, {
    s: String =>
      val json: JsValue = Json.parse(s)
      IdentityId((json \ "userId").as[String], (json \ "providerId").as[String])
  }
  )

  implicit def authMethodConvert = MappedColumnType.base[AuthenticationMethod, String](
    _.method,
    new AuthenticationMethod(_)
  )

  implicit def oauth1InfoMapper = MappedColumnType.base[OAuth1Info, String](
  {
    o: OAuth1Info =>
      val jsonified = Json.obj(
        "token" -> o.token,
        "secret" -> o.secret
      )
      Json.stringify(jsonified)
  }, {
    s: String =>
      val json: JsValue = Json.parse(s)
      OAuth1Info((json \ "token").as[String], (json \ "secret").as[String])
  }
  )

  implicit def oauth2InfoMapper = MappedColumnType.base[OAuth2Info, String](
  {
    o: OAuth2Info =>
      val jsonified = Json.obj(
        "accessToken" -> o.accessToken,
        "tokenType" -> o.tokenType,
        "expiresIn" -> o.expiresIn,
        "refreshToken" -> o.refreshToken
      )
      Json.stringify(jsonified)
  }, {
    s: String =>
      val json: JsValue = Json.parse(s)
      OAuth2Info((json \ "accessToken").as[String], (json \ "tokenType").as[Option[String]],
        (json \ "expiresIn").as[Option[Int]], (json \ "refreshToken").as[Option[String]])
  }
  )

  implicit def passwordInfoMapper = MappedColumnType.base[PasswordInfo, String](
  {
    p: PasswordInfo =>
      val jsonified = Json.obj(
        "hasher" -> p.hasher,
        "password" -> p.password,
        "salt" -> p.salt
      )
      Json.stringify(jsonified)
  }, {
    s: String =>
      val json: JsValue = Json.parse(s)
      PasswordInfo((json \ "hasher").as[String], (json \ "password").as[String],
        (json \ "salt").as[Option[String]])
  }
  )
}

trait LegendaryUserComponent extends LegendaryUserModelImplicits {

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

  def findUserByIdentityId(id: IdentityId): Option[Identity] = {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        LegendaryUsers.where(_.identityId === id).firstOption
    }
  }

  def findUserByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        val result = LegendaryUsers.where(_.email === email).firstOption
        if (result == None) {
          None
        } else if (result.get.identityId.providerId == providerId) {
          result
        } else {
          None
        }
    }
  }

  def saveNewUser(user: Identity): Identity = {
    val newUser = LegendaryUser(None, user.identityId, user.firstName, user.lastName, user.fullName, user.email, user.avatarUrl, user.authMethod, user.oAuth1Info, user.oAuth2Info, user.passwordInfo)
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session =>
        Logger.debug("Saving new user: " + user.identityId.userId)
        insert(newUser)
    }

    newUser

  }

}

object UserDAO extends LegendaryUserComponent
