package com.github.gwyddie.chat.shared
package models

import argonaut.Argonaut._
import argonaut._

import scala.util.{Failure, Success, Try}

object MessageType extends Enumeration {
  type MessageType = Value
  val UserTextMessage, UserJoinedChat, UserLeftChat, UserFailedToJoinChat = Value

  implicit lazy val messageTypeCodecJson: CodecJson[MessageType] = CodecJson[MessageType](
    mt => mt.toString.asJson,
    cursor => cursor.as[String] flatMap { charSeq =>
      Try(values.find(_.toString == charSeq).get) match {
        case Failure(exception) => DecodeResult.fail(exception.getMessage, cursor.history)
        case Success(value) => DecodeResult.ok(value)
      }
    }
  )
}
