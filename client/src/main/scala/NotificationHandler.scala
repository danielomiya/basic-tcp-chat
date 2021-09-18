package com.github.gwyddie.chat.client

import com.github.gwyddie.chat.shared.logging.LoggingMixin
import com.github.gwyddie.chat.shared.models.{Message, MessageType, UserConnection}

import scala.annotation.tailrec

class NotificationHandler(conn: UserConnection) extends Runnable with LoggingMixin {
  override def run(): Unit = {
    receiveMessage()
  }

  def handleNotification(msg: Message): Unit = {
    msg.typ match {
      case MessageType.UserFailedToJoinChat =>
        println("Failed to join chat at the moment, try again later")
        System.exit(403)
      case _ => println(MessageFormatter asText msg)
    }
  }

  @tailrec
  private def receiveMessage(): Unit = {
    if (!conn.in.hasNextLine) return
    val line = conn.in.nextLine()
    Message deserialize line foreach { message =>
      logger.fine(s"Received payload '$line'")
      handleNotification(message)
    }
    receiveMessage()
  }
}
