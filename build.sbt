import sbtassembly.AssemblyPlugin.autoImport._

// https://github.com/sbt/sbt-release/issues/219#issuecomment-426667182
// sbt-release doesn't mix well with cross scala versions and sbt assembly.
val crossScalaVersionNumbers = Seq("2.11.12", "2.12.7")

val scalaVersionNumber = crossScalaVersionNumbers.last

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.example",
  scalaVersion := scalaVersionNumber,
  test in assembly := {}
)

val gsCollectionsVersion = "6.2.0"

lazy val shaded_gs_collections = project.in(file("shaded/gs-collections"))
  .settings(commonSettings)
  .settings(
    name := "shaded-gs-collections",
    //logLevel in assembly := Level.Debug,
    test in assembly := {},
    assemblyOption in assembly ~= {
      _.copy(includeScala = false)
    },
    assemblyJarName in assembly := {
      s"${name.value}-${scalaBinaryVersion.value}-${version.value}-assembly.jar"
    },
    //target in assembly := target.value / scalaBinaryVersion.value,
    crossScalaVersions := crossScalaVersionNumbers,
    addArtifact(Artifact("shaded-gs-collections", "assembly"), sbtassembly.AssemblyKeys.assembly),
    assemblyShadeRules in assembly := Seq(
      ShadeRule.rename("com.gs.collections.**" ->
          "shadeio.@0").inAll
    ),
    libraryDependencies += "com.goldmansachs" % "gs-collections" % gsCollectionsVersion
  )

lazy val app = (project in file("app"))
  .settings(commonSettings: _*)
  .settings(
    crossScalaVersions := crossScalaVersionNumbers,
    unmanagedJars in Compile ++= Seq(
      shaded_gs_collections.base / "target" / s"scala-${scalaBinaryVersion.value}" /
        s"shaded-gs-collections-${scalaBinaryVersion.value}-${version.value}-assembly.jar"
    ),
    update := (update dependsOn (shaded_gs_collections / assembly)).value
  )
  .disablePlugins(sbtassembly.AssemblyPlugin)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "shade-with-sbt-assembly"
  )
  .aggregate(shaded_gs_collections, app)
  .disablePlugins(sbtassembly.AssemblyPlugin)


