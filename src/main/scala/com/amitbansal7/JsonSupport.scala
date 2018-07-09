package com.amitbansal7

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.mongodb.scala.bson.ObjectId
import spray.json.{ DefaultJsonProtocol, JsString, JsValue, RootJsonFormat }

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object ObjectIdSerializer extends RootJsonFormat[ObjectId] {
    override def read(json: JsValue): ObjectId = new ObjectId(json.toString)

    override def write(obj: ObjectId): JsValue = JsString(obj.toHexString)
  }

  implicit val userFormat = jsonFormat7(User.apply)
  implicit val authResFormat = jsonFormat2(AuthRes)
}
