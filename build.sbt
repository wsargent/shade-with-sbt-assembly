import Dependencies._

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.example",
  scalaVersion := "2.12.1",
  test in assembly := {},
  assemblyMergeStrategy in assembly := {
    case "META-INF/io.netty.versions.properties" =>
      MergeStrategy.first
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)

val asyncHttpClientVersion = "2.0.11"
lazy val asyncHttpClient = Seq(
  "org.asynchttpclient" % "async-http-client" % asyncHttpClientVersion,
  "org.asynchttpclient" % "netty-resolver-dns" % asyncHttpClientVersion,
  "org.asynchttpclient" % "netty-resolver" % asyncHttpClientVersion,
  "org.asynchttpclient" % "netty-codec-dns" % asyncHttpClientVersion
)

lazy val app = (project in file("app")).
  settings(commonSettings: _*).
  settings(
    mainClass := Some("example.Hello"),
    libraryDependencies ++= asyncHttpClient,
    libraryDependencies += scalaTest % Test
  )

// https://github.com/dwijnand/giter8/commit/5c9b4529d36d8b2f46323c52918f4833870a26ac
lazy val shaded = (project in file("shaded")).
  settings(commonSettings: _*).
  dependsOn(app).
  settings(
    logLevel in assembly := Level.Debug,
    name := "ahc-shaded",
    assemblyOption in assembly ~= (_.copy(includeScala = false)),
    assemblyShadeRules in assembly := Seq(
      ShadeRule.rename("io.netty.**" -> "play.ws.libs.ws.ahc.@0").inAll,
      ShadeRule.rename("org.asynchttpclient.**" -> "play.ws.libs.ws.ahc.@0").inAll
    ),
    artifact in(Compile, assembly) ~= (_.copy(classifier = Some("assembly"))),
    addArtifact(artifact in(Compile, assembly), assembly)
  )

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "Hello"
  ).aggregate(app, shaded)

