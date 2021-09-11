package com.github.gwyddie.chat.client

import scala.io.StdIn

object Program {
  def main(args: Array[String]): Unit = {
    // val host = StdIn.readLine("Type the host: ").trim
    // val port = StdIn.readLine("Type the port: ").trim.toInt
    val username = StdIn.readLine("Type your name: ").trim

    new ChatClient(username = username).start()
  }
}
