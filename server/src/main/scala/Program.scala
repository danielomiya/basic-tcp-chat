package com.github.gwyddie.chat.server

object Program {
  val PORT = 8080

  def main(args: Array[String]): Unit = {
    new ChatServer(PORT, 10).listen()
  }
}
