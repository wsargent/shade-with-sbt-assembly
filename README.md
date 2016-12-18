# Shading Libraries with SBT Assembly

This is a simple SBT project to demonstrate how to shade a library like Netty or Google Guava into another package, using [sbt assembly](https://github.com/sbt/sbt-assembly#shading). 

This project is based on [Manu Zhang](https://twitter.com/manuzhang)'s fantastic blog post [Shade with SBT](https://manuzhang.github.io/2016/10/15/shading.html).  

The blog post uses Apache Gearpump as an example and the files have moved from their original location, but some relevant commits can be found here:

*  https://github.com/huafengw/incubator-gearpump/blob/4474618c4fdd42b152d26a6915704a4f763d14c1/project/BuildShaded.scala

But Gearpump is a large project and so this strips it down to the basics of what needs to be shaded.

## Running

You must create the shaded version of the library before you compile anything:

```
sbt assembly
```

After that, you should be able to compile normally.

```
sbt compile
```

If you are using IntelliJ IDEA or another IDE, you'll need to point to the unmanaged JAR directly to avoid the IDE errors:
 
* Open the "shaded/target/2.12" directory
* Right click on `example-shaded-gs-collections-2.12-0.1-SNAPSHOT.jar`
* Click add as library
* Add it to the `app` module.

## Publishing

Still working on this.

To add the shaded project as a published maven project, you're supposed to do something like:

> We traverse the XML tree and append the shaded dependency to the <dependencies></dependencies> tags.

```scala
project(
  settings ++= Seq(
    pomPostProcess := {
        (node: xml.Node) => addShadedDeps(List(
          <dependency>
            <groupId>{organization.value}</groupId>
            <artifactId>{shaded_guava.id}</artifactId>
            <version>{version.value}</version>
          </dependency>
        ), node)
    }
 )
)
 
private def addShadedDeps(deps: Seq[xml.Node], node: xml.Node): xml.Node = {
  node match {
    case elem: xml.Elem =>
      val child = if (elem.label == "dependencies") {
        elem.child ++ deps
      } else {
        elem.child.map(addShadedDeps(deps, _))
      }
      xml.Elem(elem.prefix, elem.label, elem.attributes, elem.scope, false, child: _*)
    case _ =>
      node
  }
} 
```
