# Shading Libraries with SBT Assembly

This is a simple SBT project to demonstrate how to shade a library like Netty or Google Guava into another package, using [sbt assembly](https://github.com/sbt/sbt-assembly#shading). 

This project is based on [Manu Zhang](https://twitter.com/manuzhang)'s fantastic blog posts:
 
* [Shade with SBT](https://manuzhang.github.io/2016/10/15/shading.html)
* [Shade with SBT II](https://manuzhang.github.io/2016/11/12/shading-2.html)

Also see the following

* http://asyncified.io/2016/04/07/spark-uber-jars-and-shading-with-sbt-assembly/
* http://queirozf.com/entries/creating-scala-fat-jars-for-spark-on-sbt-with-sbt-assembly-plugin

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
