import sbtassembly.AssemblyPlugin.autoImport._

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.example",
  scalaVersion := "2.12.1",
  test in assembly := {}
)

val crossScalaVersionNumbers = Seq("2.12.1")
val scalaVersionNumber = crossScalaVersionNumbers.last

val gsCollectionsVersion = "6.2.0"
val scalaVersionMajor = "2.12"

val shadeAssemblySettings = commonSettings ++ Seq(
  logLevel in assembly := Level.Debug,
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
).aggregate(shaded_gs_collections).disablePlugins(sbtassembly.AssemblyPlugin)

lazy val shaded_gs_collections = Project(
  id = "example-shaded-gs-collections",
  base = file("shaded/gs-collections"),
  settings = shadeAssemblySettings ++ addArtifact(Artifact("example-shaded-gs-collections",
    "assembly"), sbtassembly.AssemblyKeys.assembly) ++
      Seq(
        assemblyShadeRules in assembly := Seq(
          ShadeRule.rename("com.gs.collections.**" ->
              "org.apache.example.gs.collections.@1").inAll
        )
      ) ++
      Seq(
        libraryDependencies ++= Seq(
          "com.goldmansachs" % "gs-collections" % gsCollectionsVersion
        )
      )
)

def getShadedJarFile(name: String, gearpumpVersion: String): File = {
  shaded.base / "target" / scalaVersionMajor /
    s"example-shaded-$name-$scalaVersionMajor-$gearpumpVersion-assembly.jar"
}

val streamingDependencies = Seq(
  unmanagedJars in Compile ++= Seq(
    getShadedJarFile("gs-collections", version.value)
  )
)

lazy val app = (project in file("app"))
  .settings(commonSettings: _*)
  .settings(streamingDependencies)
  .disablePlugins(sbtassembly.AssemblyPlugin)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "example-shaded-assembly"
  ).aggregate(shaded, app)
  .disablePlugins(sbtassembly.AssemblyPlugin)


