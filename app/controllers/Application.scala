package controllers

import java.io.File

import play.Play
import play.api.mvc.Action
import play.api.mvc.Controller

/*
 * Author: Sari Haj Hussein
 */

object Application extends Controller {
  
  /** serve the index page app/views/index.scala.html */
  def index(any: String) = Action {
    Ok(views.html.index())
  }
}