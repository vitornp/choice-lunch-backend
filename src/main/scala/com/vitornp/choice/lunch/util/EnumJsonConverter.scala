package com.vitornp.choice.lunch.util

import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

class EnumJsonConverter[T <: scala.Enumeration](enu: T) extends RootJsonFormat[T#Value] {

  def write(obj: T#Value) = JsString(obj.toString)

  def read(json: JsValue): T#Value = {
    json match {
      case JsString(txt) => enu.withName(txt)
      case something => throw DeserializationException(s"Expected a value from enum $enu instead of $something")
    }
  }
}
