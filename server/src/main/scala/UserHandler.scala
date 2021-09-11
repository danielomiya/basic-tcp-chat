package com.github.gwyddie.chat.server

import com.github.gwyddie.chat.shared.logging.LoggingMixin
import com.github.gwyddie.chat.shared.models.{Message, MessageType, UserConnection}

import java.net.Socket
import java.time.OffsetDateTime
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}
import scala.util.control.Breaks.{break, breakable}

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
