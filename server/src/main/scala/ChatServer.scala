package com.github.gwyddie.chat.server

import com.github.gwyddie.chat.shared.logging.LoggingMixin

import java.net.ServerSocket
import java.util.concurrent.{ExecutorService, Executors}
import scala.annotation.tailrec

class ChatServer(port: Int, poolSize: Int = 10) extends LoggingMixin {
  val serverSocket = new ServerSocket(port, poolSize)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)

  def listen(): Unit = {
    logger.info(s"Listening on port $port")

    try {
      acceptConnections()
    } finally {
      pool.shutdown()
    }
  }

  @tailrec
  private def acceptConnections(): Unit = {
    val socket = serverSocket.accept()
    logger.info(s"Established connection from ${socket.getInetAddress}")
    pool execute new UserHandler(socket, this)
    acceptConnections()
  }
}
