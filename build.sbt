import Dependencies._

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.example",
  scalaVersion := "2.12.1",
  test in assembly := {}
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "Hello"
  ).aggregate(app, utils)


lazy val app = (project in file("app")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies += scalaTest % Test,
    mainClass in assembly := Some("example.Hello")
  ).settings(
    artifact in (Compile, assembly) ~= { art =>
      art.copy(`classifier` = Some("assembly"))
    }
  ).settings(addArtifact(artifact in (Compile, assembly), assembly).settings: _*)

lazy val utils = (project in file("utils")).
  settings(commonSettings: _*).
  settings(
    assemblyJarName in assembly := "utils.jar"
    // more settings here ...
  )