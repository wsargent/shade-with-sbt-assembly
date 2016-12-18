# Shading Libraries with SBT Assembly

This is a simple SBT project to demonstrate how to shade a library like Netty or Google Guava into another package, using [sbt assembly](https://github.com/sbt/sbt-assembly#shading). 

This project is based on [Manu Zhang](https://manuzhang.github.io)'s blog post [Shade with SBT](https://manuzhang.github.io/2016/10/15/shading.html).  The blog post uses Apache Gearpump as an example -- the relevant change can be found here:

*  https://github.com/huafengw/incubator-gearpump/blob/4474618c4fdd42b152d26a6915704a4f763d14c1/project/BuildShaded.scala

But Gearpump is a large project and so this strips it down to the basics of what needs to be shaded.

