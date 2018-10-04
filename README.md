# Shading Libraries with SBT Assembly

This is a simple SBT project to demonstrate how to shade a library like Netty or Google Guava into another package, using [sbt assembly](https://github.com/sbt/sbt-assembly#shading). 

This project is based on [Manu Zhang](https://twitter.com/manuzhang)'s fantastic blog posts:
 
* [Shade with SBT](https://manuzhang.github.io/2016/10/15/shading.html)
* [Shade with SBT II](https://manuzhang.github.io/2016/11/12/shading-2.html)

The blog post uses Apache Gearpump as an example and the files have moved from their original location, but some relevant commits can be found here:

*  https://github.com/huafengw/incubator-gearpump/blob/4474618c4fdd42b152d26a6915704a4f763d14c1/project/BuildShaded.scala

But Gearpump is a large project and so this strips it down to the basics of what needs to be shaded.

## Running

The shading is handled in `shaded_gs_collections` and the update task is overridden to ensure that shading and creation of jar is done before the `compile` stage, i.e.

```scala
update := (update dependsOn (shaded_gs_collections / assembly)).value
```

Compiling works as per usual:

```bash
sbt compile
```

You can also publish locally:

```bash
sbt publishLocal
```

## Publishing

Cross publishing is something that doesn't work really well with sbt-release and sbt-assembly, because of https://github.com/sbt/sbt-release/issues/219 -- the best way to fix this is to always define your `scalaCrossVersions` in each project individually.

```
sbt release
```

and then to run it

```bash
cd /home/wsargent/work/shade-with-sbt-assembly/app/target/release-repo/com/example/app_2.12/1.0.0
java -jar app_2.12-1.0.0-assembly.jar 
```