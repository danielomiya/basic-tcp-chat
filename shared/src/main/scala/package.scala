package com.github.gwyddie.chat

import argonaut.Argonaut._
import argonaut._

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}

package object shared {
  implicit lazy val offsetDateTimeCodecJson: CodecJson[OffsetDateTime] = CodecJson[OffsetDateTime](
    odt => odt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME).asJson,
    cursor => cursor.as[String].flatMap { charSeq =>
      Try(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(charSeq, OffsetDateTime.from(_))) match {
        case Failure(exception) => DecodeResult.fail(exception.getMessage, cursor.history)
        case Success(value) => DecodeResult.ok(value)
      }
    }
  )
}
