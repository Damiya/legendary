package controllers

import play.api.db._
import play.api.Play.current
import play.api.mvc.Action
import play.api.mvc.Controller
import play.Logger

object Application extends Controller {

  /** serve the index page app/views/index.scala.html */
  def index(any: String) = Action {
    Ok(views.html.index())
  }

  def test() = Action {
    DB.withConnection {
      conn =>
        Logger.info("Hellow orld")
    }
    Ok("ok")
  }
}