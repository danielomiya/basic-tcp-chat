package com.github.gwyddie.chat.server

import scala.io.StdIn

object Program {
  def main(args: Array[String]): Unit = {
    val port = StdIn.readLine("Type the port you'd like to listen to: ").trim.toInt
    val concurrency = StdIn.readLine("Type the max number of concurrent users: ").trim.toInt
    new ChatServer(port, concurrency).listen()
  }
}
