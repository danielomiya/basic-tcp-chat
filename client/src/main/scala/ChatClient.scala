package com.github.gwyddie.chat.client

import com.github.gwyddie.chat.shared.logging.LoggingMixin
import com.github.gwyddie.chat.shared.models.{Message, MessageType, UserConnection}

import java.net.Socket
import java.time.OffsetDateTime
import java.util.Scanner
import java.util.concurrent.{ExecutorService, Executors}
import scala.annotation.tailrec

class ChatClient(host: String = "127.0.0.1", port: Int = 8080, username: String, poolSize: Int = 5)
  extends LoggingMixin {

  val socket = new Socket(host, port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)

  def start(): Unit = {
    val conn = UserConnection(username, socket)
    val keyboard = new Scanner(System.in)

    pool execute new NotificationHandler(conn)
    conn.out.println(createMessage(MessageType.UserJoinedChat).asJson)

    takeInput(keyboard, conn)
  }

  def createMessage(text: String, typ: MessageType.Value = MessageType.UserTextMessage): Message =
    Message(username = username, payload = Option(text), typ = typ, sentAt = OffsetDateTime.now())

  def createMessage(typ: MessageType.Value): Message = createMessage(null, typ)

  @tailrec
  private def takeInput(keyboard: Scanner, conn: UserConnection): Unit = {
    if (!keyboard.hasNextLine) return
    val text = keyboard.nextLine().trim
    val typ =
      if (text startsWith "/leave") MessageType.UserLeftChat
      else MessageType.UserTextMessage
    val message = createMessage(text, typ)

    if (message.isValid) {
      conn.out.println(message.asJson)

      if (typ == MessageType.UserLeftChat) {
        conn.close()
        System.exit(0)
      }
    }
    takeInput(keyboard, conn)
  }
}
