package com.itsdamiya.legendary.models

import play.api.db.slick.Config.driver.simple._

private[models] trait DAO {
  val Users = TableQuery[Users]
}