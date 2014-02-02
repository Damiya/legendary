import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "legendary"
  val appVersion = "0.0.4-SNAPSHOT"

  val appDependencies = Seq(
    "com.typesafe.slick" %% "slick" % "2.0.0",
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "joda-time" % "joda-time" % "2.3 ",
    "org.joda" % "joda-convert" % "1.5",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "com.github.tototoshi" %% "slick-joda-mapper" % "1.0.0",
    filters,
    jdbc
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalacOptions ++= Seq("-feature", "-language:postfixOps", "-language:reflectiveCalls")
  )

}
