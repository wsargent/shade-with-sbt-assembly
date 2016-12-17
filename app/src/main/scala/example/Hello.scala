package example

import org.asynchttpclient._

object Hello {

  def main(args: Array[String]): Unit = {
    val config = new DefaultAsyncHttpClientConfig.Builder().build()
    val client = new DefaultAsyncHttpClient(config)

    println(s"Hello $client")

    client.close()
  }

}
