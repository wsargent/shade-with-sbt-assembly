package example

import shadeio.com.gs.collections.impl.list.mutable.FastList

object Hello {

  def main(args: Array[String]): Unit = {
    val people = FastList.newListWith("person1", "person2", "person3")

    println(s"Hello $people")
  }

}
