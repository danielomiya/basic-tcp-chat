package com.github.gwyddie.chat.client

import com.github.gwyddie.chat.shared.models.{Message, MessageType}

import java.time.format.DateTimeFormatter

object MessageFormatter {
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")

  def asText(msg: Message): String = s"${getPrefix(msg)}${getContent(msg)}${getSuffix(msg)}"

  def getPrefix(msg: Message) = {
    val formattedDate = dateTimeFormatter.format(msg.receivedAt getOrElse msg.sentAt)
    s"[$formattedDate] "
  }

  def getContent(msg: Message): String = msg.typ match {
    case MessageType.UserTextMessage => s"${msg.username} says: ${msg.payload getOrElse ""}"
    case MessageType.UserJoinedChat => s"${msg.username} has joined the chat"
    case MessageType.UserLeftChat => s"${msg.username} has left the chat"
    case _ => "ERROR: UNRECOGNIZED MESSAGE TYPE"
  }

  def getSuffix(msg: Message): String = ""
}
