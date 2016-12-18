import Dependencies._

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.example",
  scalaVersion := "2.12.1"
)

val asyncHttpClientVersion = "2.0.11"
lazy val asyncHttpClient = Seq(
  "org.asynchttpclient" % "async-http-client" % asyncHttpClientVersion,
  "org.asynchttpclient" % "netty-resolver-dns" % asyncHttpClientVersion,
  "org.asynchttpclient" % "netty-resolver" % asyncHttpClientVersion,
  "org.asynchttpclient" % "netty-codec-dns" % asyncHttpClientVersion
)

lazy val app = (project in file("app"))
  .settings(commonSettings: _*)
  .settings(
      libraryDependencies ++= asyncHttpClient,
      libraryDependencies += scalaTest % Test
    )
  .settings(
    test in assembly := {},
    assemblyMergeStrategy in assembly := {
      case "META-INF/io.netty.versions.properties" =>
        MergeStrategy.first
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    mainClass in assembly := Some("example.Hello"),
    logLevel in assembly := Level.Debug,
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
  ).aggregate(app)

