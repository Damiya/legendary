import play.Project._
import sbt._
import sbt.Keys._
import org.scalastyle.sbt.ScalastylePlugin
import com.typesafe.sbt.SbtScalariform

object BuildSettings {
  val appOrganization = "com.itsdamiya"

  val appName = "Legendary"
  val appVersion = "0.0.6-SNAPSHOT"

  val commonResolvers = Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    "spray repo" at "http://repo.spray.io"
  )

  val commonSettings = Seq(
    organization := appOrganization,
    scalacOptions ++= Seq("-feature", "-deprecation", "-language:postfixOps", "-language:reflectiveCalls", "-language:implicitConversions", "-Xlint"),
    resolvers ++= commonResolvers
  ) ++ ScalastylePlugin.Settings ++ SbtScalariform.defaultScalariformSettings

  val commonDeps = Seq(
    "com.twitter" %% "util-collection" % "6.3.6",
    json,
    "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
    "org.scalacheck" %% "scalacheck" % "1.11.3" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test"
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
    ws
  ) ++ commonDeps


  val fateClasherDeps = Seq(
    "io.spray" % "spray-client" % "1.3-RC1",
    "com.typesafe.akka" %% "akka-actor" % "2.3.0-RC1"
  ) ++ commonDeps


}

object Build extends Build {

  import BuildSettings._

  lazy val FateClasherProject = Project("LibFateClasher", file("LibFateClasher"))
    .settings(commonSettings: _*)
    .settings(libraryDependencies := fateClasherDeps)

  lazy val LegendaryCoreProject = play.Project("Legendary-Core", appVersion, coreDeps, path = file("Legendary-Core"))
    .settings(commonSettings: _*)
    .dependsOn(FateClasherProject)

  lazy val root = Project("Legendary", file("."))
    .settings(commonSettings: _*)
    .aggregate(Seq[ProjectReference](FateClasherProject, LegendaryCoreProject): _*)
}
