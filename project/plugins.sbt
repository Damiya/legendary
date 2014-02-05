

// Comment to get more information during initialization
logLevel := Level.Warn

resolvers ++= Seq(
  Resolver.url("sbt snapshot maven", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.mavenStylePatterns),
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)



// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3-SNAPSHOT")