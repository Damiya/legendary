import scala.slick.driver.PostgresDriver.simple._

import play.api.db._
import play.api._
import models.DAO._

import scala.slick.jdbc.meta.MTable

object Global extends GlobalSettings {

  import play.api.Play.current

  override def onStart(app: Application) {
    Database.forDataSource(DB.getDataSource()).withSession {
      implicit session: Session =>
        if (MTable.getTables("legendaryUsers").list().isEmpty) {
          LegendaryUsers.ddl.create
        }
    }
  }
}