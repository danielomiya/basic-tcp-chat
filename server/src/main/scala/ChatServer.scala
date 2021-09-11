package com.github.gwyddie.chat.server

import com.github.gwyddie.chat.shared.logging.LoggingMixin

import java.net.ServerSocket
import java.util.concurrent.{ExecutorService, Executors}
import scala.language.postfixOps

class ChatServer(port: Int, poolSize: Int = 10) extends LoggingMixin {
  val serverSocket = new ServerSocket(port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)

  def listen(): Unit = {
    logger.info(s"Listening on port $port")
    try {
      while (true) {
        val socket = serverSocket.accept()
        logger.info(s"Established connection from ${socket.getInetAddress}")
        pool execute new UserHandler(socket, this)
      }
    } finally {
      pool.shutdown()
    }
  }


}
