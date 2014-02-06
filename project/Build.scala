import io.apigee.trireme.fromnode.path
import sbt._

import sbt._
import Keys._
import play.Project._

object BuildSettings {
  val appOrganization = "com.itsdamiya"

  val appName = "Legendary"
  val appVersion = "0.0.5-SNAPSHOT"

  val commonResolvers = Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  val commonSettings = Seq(
    organization := appOrganization,
    scalacOptions ++= Seq("-feature", "-deprecation", "-language:postfixOps", "-language:reflectiveCalls", "-language:implicitConversions"),
    javacOptions ++= Seq("-deprecation"),
    resolvers ++= commonResolvers
  )

  val coreDeps = Seq(
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

  val fateClasherDeps = Seq(
    "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
    "org.scalacheck" %% "scalacheck" % "1.11.3" % "test",
    "com.typesafe.akka" %% "akka-actor" % "2.2.3",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test"
  )
}

object Build extends Build {

  import BuildSettings._

  lazy val FateClasherProject = Project("LibFateClasher", file("LibFateClasher"))
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= fateClasherDeps)

  lazy val LegendaryCoreProject = play.Project("Legendary-Core", appVersion, coreDeps, path = file("Legendary-Core"))
    .settings(commonSettings: _*)
    .dependsOn(FateClasherProject)

  lazy val root = Project("Legendary", file("."))
    .settings(commonSettings: _*)
    .aggregate(Seq[ProjectReference](FateClasherProject, LegendaryCoreProject): _*)
}
