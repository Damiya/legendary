import scala.slick.driver.PostgresDriver.simple._

import play.api.db._
import play.api._
import models.{LegendaryUserDAO,LegendaryTokenDAO}

import scala.slick.jdbc.meta.MTable

object Global extends GlobalSettings {

  import play.api.Play.current

  override def onStart(app: Application) {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session: Session =>
        if (MTable.getTables("legendaryUsers").list().isEmpty) {
          LegendaryUserDAO.LegendaryUsers.ddl.create
        }

        if (MTable.getTables("legendaryTokens").list().isEmpty) {
          LegendaryTokenDAO.LegendaryTokens.ddl.create
        }
    }
  }
}