package com.github.gwyddie.chat.server

import com.github.gwyddie.chat.shared.logging.LoggingMixin
import com.github.gwyddie.chat.shared.models.{Message, MessageType, UserConnection}

import java.net.Socket
import java.time.OffsetDateTime
import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}

class UserHandler(socket: Socket, server: ChatServer) extends Runnable with LoggingMixin {

  implicit val scalaPool: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(server.pool)

  import UserHandler._

  override def run(): Unit = {
    val conn = UserConnection(null, socket)
    val line = conn.in.nextLine()
    val joinMessage = Message deserialize line

    joinMessage match {
      case None =>
        logger.info(s"Validation error from ${socket.getInetAddress}")
        conn.close()
      case Some(value) =>
        if (usersMap contains value.username) {
          logger.severe(s"User ${value.username} is already logged in, closing connection")
          conn.out.println(
            value.copy(
              typ = MessageType.UserFailedToJoinChat,
              sentAt = OffsetDateTime.now(),
              payload = Some("User is already logged in"),
            ).asJson
          )
          conn.close()
        } else {
          usersMap put(value.username, conn.copy(username = value.username))
          notifyClients(socket, value.copy(receivedAt = Some(OffsetDateTime.now())))
          receiveMessages(conn, value.username)
        }
    }
  }

  @tailrec
  private def receiveMessages(conn: UserConnection, username: String): Unit = {
    val content = conn.in.nextLine()

    Message.deserialize(content) match {
      case Some(message) =>
        val enrichedMessage = message.copy(receivedAt = Some(OffsetDateTime.now()))
        notifyClients(socket, enrichedMessage)

        if (message.typ == MessageType.UserLeftChat) {
          logger.info(s"Closed connection from ${socket.getInetAddress}")
          usersMap remove username
          conn.close()
        } else {
          receiveMessages(conn, username)
        }
      case None =>
        logger.severe(s"Received invalid payload: $content")
    }
  }
}

object UserHandler extends LoggingMixin {
  val usersMap: mutable.Map[String, UserConnection] = mutable.Map.empty

  def notifyClients(origin: Socket, message: Message)(implicit executor: ExecutionContext): Unit = Future {
    val json = message.asJson
    logger.info(s"Broadcasting message: '$json'")
    usersMap foreach { case (_, conn) =>
      conn.out.println(json)
    }
  }
}
