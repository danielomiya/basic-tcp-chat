package com.github.gwyddie.chat.client

import com.github.gwyddie.chat.shared.logging.LoggingMixin
import com.github.gwyddie.chat.shared.models.{Message, UserConnection}

class NotificationHandler(conn: UserConnection) extends Runnable with LoggingMixin {
  override def run(): Unit = {
    while (conn.in.hasNextLine) {
      val line = conn.in.nextLine()
      Message deserialize line foreach { message =>
        logger.fine(s"Received payload '$line'")
        println(MessageFormatter asText message)
      }
    }
  }
}
