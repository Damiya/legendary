

// Comment to get more information during initialization
logLevel := Level.Warn

resolvers ++= Seq(
  Resolver.url("sbt snapshot maven", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns),
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.url("typesafe snapshot ivy", url("http://repo.typesafe.com/typesafe/snapshots"))(Resolver.ivyStylePatterns),
  "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"
)



scalacOptions ++= Seq("-deprecation")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.2")

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3-SNAPSHOT")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.3.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.2.1")
