import sbt.RepositoryHelpers.FileConfiguration

// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += Resolver.url("sbt snapshot maven", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.mavenStylePatterns)

//
//def generateLocalPattern() = {
//  val pList = ("${ivy.home}/local/[organisation]/[module]_[scalaVersion]/[revision]/[type]s/[module]_[scalaVersion].[artifact]") :: Nil
//  FileRepository("Local Publish", FileConfiguration(true, None), Patterns(pList, pList, false))
//}
//
//resolvers += generateLocalPattern()

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3-SNAPSHOT")