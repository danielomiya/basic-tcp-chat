package com.github.gwyddie.chat.server

import com.github.gwyddie.chat.shared.logging.LoggingMixin
import com.github.gwyddie.chat.shared.models.{Message, MessageType, UserConnection}

import java.net.{ServerSocket, Socket}
import java.time.OffsetDateTime
import java.util.concurrent.{ExecutorService, Executors}
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}
import scala.language.postfixOps
import scala.util.control.Breaks.{break, breakable}

class ChatServer(port: Int, poolSize: Int = 10) extends LoggingMixin {
  val serverSocket = new ServerSocket(port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)
  val scalaPool: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(pool)

  // val clients: mutable.Set[Socket] = mutable.Set.empty
  // val clientsMap: mutable.Map[String, Socket] = mutable.Map.empty
  val usersMap: mutable.Map[String, UserConnection] = mutable.Map.empty

  def listen(): Unit = {
    logger.info(s"Listening on port $port")
    try {
      while (true) {
        val socket = serverSocket.accept()
        logger.info(s"Established connection from ${socket.getInetAddress}")
        pool execute new UserHandler(socket)
      }
    } finally {
      pool.shutdown()
    }
  }

  def notifyClients(origin: Socket, message: Message): Unit = Future {
    val json = message.asJson
    logger.info(s"Broadcasting message: '$json'")
    usersMap foreach { case (_, conn) =>
      conn.out.println(json)
    }
  }(scalaPool)

  class UserHandler(socket: Socket) extends Runnable {
    override def run(): Unit = {
      val conn = UserConnection(null, socket)
      val line = conn.in.nextLine()
      val joinMessage = Message deserialize line

      joinMessage match {
        case None =>
          logger.info(s"Validation error from ${socket.getInetAddress}")
          conn.close()
        case Some(value) =>
          usersMap.put(value.username, conn.copy(username = value.username))
          notifyClients(socket, value.copy(receivedAt = Some(OffsetDateTime.now())))

          breakable {
            while (conn.in.hasNextLine) { // block until there is a new line
              val content = conn.in.nextLine()

              Message.deserialize(content) foreach { message =>
                val enrichedMessage = message.copy(receivedAt = Some(OffsetDateTime.now()))
                notifyClients(socket, enrichedMessage)

                if (message.typ == MessageType.UserLeftChat) {
                  logger.info(s"Closed connection from ${socket.getInetAddress}")
                  // clients.remove(socket)
                  // clientsMap.remove(message.username)
                  usersMap.remove(value.username)
                  conn.close()
                  break
                }
              }
            }
          }
      }
    }
  }
}
