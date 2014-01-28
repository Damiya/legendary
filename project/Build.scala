import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "legendary"
  val appVersion = "0.1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.typesafe.slick" %% "slick" % "2.0.0",
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "ws.securesocial" %% "securesocial" % "2.1.3",
    jdbc
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(

  )

}
