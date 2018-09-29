import sbtassembly.AssemblyPlugin.autoImport._

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.example",
  scalaVersion := "2.12.1",
  test in assembly := {}
)

val crossScalaVersionNumbers = Seq("2.12.1")
val scalaVersionNumber = crossScalaVersionNumbers.last

val scalaVersionMajor = "2.12"

val shadeAssemblySettings = commonSettings ++ Seq(
  logLevel in assembly := Level.Info,
  test in assembly := {},
  assemblyOption in assembly ~= {
    _.copy(includeScala = false)
  },
  assemblyJarName in assembly := {
    s"${name.value}-$scalaVersionMajor-${version.value}-assembly.jar"
  },
  target in assembly := baseDirectory.value.getParentFile / "target" / scalaVersionMajor
)

lazy val shaded = Project(
  id = "example-shaded",
  base = file("shaded")
).aggregate(shaded_bytebuddy).disablePlugins(sbtassembly.AssemblyPlugin)

lazy val shaded_bytebuddy = Project(
  id = "shaded-bytebuddy",
  base = file("shaded/bytebuddy"),
  settings = shadeAssemblySettings ++ addArtifact(Artifact("shaded-bytebuddy",
    "assembly"), sbtassembly.AssemblyKeys.assembly) ++
      Seq(
        assemblyShadeRules in assembly := Seq(
          ShadeRule.rename("net.bytebuddy.**" ->
              "shadeio.@0").inAll
        )
      ) ++
      Seq(
        libraryDependencies ++= Seq(
          "net.bytebuddy" % "byte-buddy" % "1.8.22",
          "net.bytebuddy" % "byte-buddy-agent" % "1.8.22"
        )
      )
)

def getShadedJarFile(name: String, version: String): File = {
  shaded.base / "target" / scalaVersionMajor /
    s"shaded-$name-$scalaVersionMajor-$version-assembly.jar"
}

val shadedDependencies = Seq(
  unmanagedJars in Compile ++= Seq(
    getShadedJarFile("bytebuddy", version.value)
  )
)

lazy val app = (project in file("app"))
  .settings(commonSettings: _*)
  .settings(shadedDependencies)
  .settings(
    crossPaths := false,
    autoScalaLibrary := false,
    mainClass in assembly := Some("example.Hello"),
    libraryDependencies += "com.typesafe" % "config" % "1.3.2",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
  ).aggregate(shaded)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "shade-with-sbt-assembly"
  ).aggregate(shaded, app)
  .disablePlugins(sbtassembly.AssemblyPlugin)


