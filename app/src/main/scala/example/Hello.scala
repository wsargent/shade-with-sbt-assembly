package example

import org.apache.gearpump.gs.collections.impl.list.mutable.FastList

import scala.collection.mutable

object Hello {

  def main(args: Array[String]): Unit = {
    val people = FastList.newListWith("person1", "person2", "person3");

    println(s"Hello $people")
  }

}
