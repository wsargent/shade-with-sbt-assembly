import Dependencies._

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.example",
  scalaVersion := "2.12.1",
  test in assembly := {}
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
    libraryDependencies ++= asyncHttpClient,
    libraryDependencies += scalaTest % Test
  )

lazy val utils = (project in file("utils")).
  settings(commonSettings: _*).
  settings(
    assemblyJarName in assembly := "utils.jar"
    // more settings here ...
  )

// https://github.com/dwijnand/giter8/commit/5c9b4529d36d8b2f46323c52918f4833870a26ac
lazy val shaded = (project in file("shaded")).
  settings(commonSettings: _*).
  dependsOn(app).
  settings(
    name := "giter8-shaded",
    assemblyOption in assembly ~= (_.copy(includeScala = false)),
    assemblyShadeRules in assembly := Seq(
      ShadeRule.rename("io.netty.**" -> "giter8.netty.@1").inAll,
      ShadeRule.rename("org.asynchttpclient.**" -> "giter8.asynchttpclient.@1").inAll
    ),
    artifact in(Compile, assembly) ~= (_.copy(classifier = Some("assembly"))),
    addArtifact(artifact in(Compile, assembly), assembly),
    pomPostProcess := {
      import scala.xml._
      import transform._
      node =>
        new RuleTransformer(new RewriteRule {
          override def transform(node: Node): NodeSeq = node match {
            case e: Elem if e.label == "dependencies" =>
              <dependencies>
                <dependency>
                  <groupId>org.scala-lang</groupId>
                  <artifactId>scala-library</artifactId>
                  <version>
                    {scalaVersion.value}
                  </version>
                </dependency>
                <dependency>
                  <groupId>org.scala-lang</groupId>
                  <artifactId>scala-reflect</artifactId>
                  <version>
                    {scalaVersion.value}
                  </version>
                </dependency>
              </dependencies>
            case other => other
          }
        }).transform(node).head
    }
  )

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "Hello"
  ).aggregate(app, utils, shaded)

