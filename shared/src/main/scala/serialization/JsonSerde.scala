package com.github.gwyddie.chat.shared
package serialization

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson}

trait JsonSerde[T] {
  def asJson(entity: T)(implicit e: EncodeJson[T]): String = entity.asJson.nospaces

  def deserialize(json: String)(implicit d: DecodeJson[T]): Option[T] = json.decodeOption[T]
}
