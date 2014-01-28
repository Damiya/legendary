package models

import securesocial.core._
import play.api.libs.json.{JsValue, Json}
import securesocial.core.IdentityId
import securesocial.core.OAuth2Info
import securesocial.core.OAuth1Info
import scala.slick.driver.PostgresDriver.simple._

trait ModelImplicits {


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
