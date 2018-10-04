import sbtassembly.AssemblyPlugin.autoImport._

// https://github.com/sbt/sbt-release/issues/219#issuecomment-426667182
// sbt-release doesn't mix well with cross scala versions and sbt assembly.
val crossScalaVersionNumbers = Seq("2.11.12", "2.12.7")

val scalaVersionNumber = crossScalaVersionNumbers.last

lazy val commonSettings = Seq(
  organization := "com.example",
  scalaVersion := scalaVersionNumber,
  test in assembly := {}
)

val disablePublishing = Seq[Setting[_]](
  publishArtifact := false,
  // The above is enough for Maven repos but it doesn't prevent publishing of ivy.xml files
  publish := {},
  publishLocal := {},
)

val gsCollectionsVersion = "6.2.0"

lazy val shaded_gs_collections = project.in(file("shaded/gs-collections"))
  .settings(commonSettings)
  .settings(disablePublishing)
  .settings(
    name := "shaded-gs-collections",
    //logLevel in assembly := Level.Debug,
    test in assembly := {},
    assemblyOption in assembly ~= {
      _.copy(includeScala = false) // java libraries shouldn't include scala
    },
    assemblyJarName in assembly := {
      s"${name.value}-${scalaBinaryVersion.value}-${version.value}-assembly.jar"
    },
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
    releaseCrossBuild := true,
    crossScalaVersions := crossScalaVersionNumbers,
    unmanagedJars in Compile ++= Seq(
      shaded_gs_collections.base / "target" / s"scala-${scalaBinaryVersion.value}" /
        s"shaded-gs-collections-${scalaBinaryVersion.value}-${version.value}-assembly.jar"
    ),
    update := (update dependsOn (shaded_gs_collections / assembly)).value,
    // If you are using a maven repository
    // https://www.scala-sbt.org/1.x/docs/Publishing.html
    publishMavenStyle := true,
    publishTo := Some(Resolver.file("file",  baseDirectory.value / "target" / "release-repo" )),

    // https://www.scala-sbt.org/1.x/docs/Artifacts.html
    // publish the assembled artifact if it has the classifier "assembly" on it.
    artifact in (Compile, assembly) := {
      val art = (artifact in (Compile, assembly)).value
      art.withClassifier(Some("assembly"))
    },
    assemblyJarName in assembly := {
      s"${name.value}-${version.value}-assembly.jar"
    },

    addArtifact(artifact in (Compile, assembly), assembly),
    packageOptions in (Compile, packageBin) ++= Seq(
      Package.ManifestAttributes("Main-Class"   -> "example.Hello"),
    ),
  )
  .enablePlugins(AssemblyPlugin)

// Type "sbt release" to have the app published"
lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(disablePublishing)
  .settings(
    name := "shade-with-sbt-assembly"
  )
  .aggregate(shaded_gs_collections, app)
  .disablePlugins(sbtassembly.AssemblyPlugin)


