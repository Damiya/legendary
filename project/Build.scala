import sbt._

import sbt._
import Keys._
import play.Project._


object ApplicationBuild extends Build {

  val appName = "legendary"
  val appVersion = "0.0.5"

  val appDependencies = Seq(
    "com.typesafe.slick" %% "slick" % "2.0.0",
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "joda-time" % "joda-time" % "2.3 ",
    "org.joda" % "joda-convert" % "1.5",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "com.github.tototoshi" %% "slick-joda-mapper" % "1.0.0",
    "com.typesafe.play" %% "play-slick" % "0.6.0-SNAPSHOT",
    "net.sf.ehcache" % "ehcache-core" % "2.6.8",
    filters,
    jdbc,
    json,
    ws
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalacOptions ++= Seq("-feature", "-language:postfixOps", "-language:reflectiveCalls"),
    resolvers +=
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

  )

}
