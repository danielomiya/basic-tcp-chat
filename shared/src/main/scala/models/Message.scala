package com.github.gwyddie.chat.shared
package models

import serialization.JsonSerde

import argonaut.Argonaut._
import argonaut._

import java.time.OffsetDateTime

case class Message(username: String,
                   typ: MessageType.Value,
                   sentAt: OffsetDateTime,
                   payload: Option[String] = None,
                   receivedAt: Option[OffsetDateTime] = None) {
  def asJson: String = Message asJson this

  def isValid: Boolean = typ != MessageType.UserTextMessage || payload.exists(_.nonEmpty)
}

object Message extends JsonSerde[Message] {
  implicit lazy val messageCodecJson: CodecJson[Message] =
    casecodec5(Message.apply, Message.unapply)("username", "type", "sent_at", "payload", "received_at")
      .setName("com.github.gwyddie.chat.shared.models.Message")
}
