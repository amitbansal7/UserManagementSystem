package com.amitbansal7

import org.mongodb.scala.bson.ObjectId
import org.apache.commons.codec.digest.DigestUtils

object User {

  def getPasshash(password: String): String =
    DigestUtils.sha256Hex(password)

  def apply(
    _id: ObjectId,
    email: String,
    username: String,
    password: String,
    firstName: Option[String],
    lastName: Option[String],
    birthDate: Option[String]
  ): User = new User(_id, username, email, password, firstName, lastName, birthDate)

  def apply(
    email: String,
    username: String,
    password: String,
    firstName: Option[String],
    lastName: Option[String],
    birthDate: Option[String]
  ): User = {
    new User(new ObjectId(), username, email, getPasshash(password), firstName, lastName, birthDate)
  }
}

case class User(
    _id: ObjectId,
    email: String,
    username: String,
    password: String,
    firstName: Option[String],
    lastName: Option[String],
    birthDate: Option[String]
) {

}

case class GetUser(email: String)

case object GetAllUsers

case class AddUser(user: User)

case class DeleteOne(email: String)

case class UserResponse(users: Seq[User])

case class Authenticate(username: Option[String], email: Option[String], password: String)

case class AuthRes(res: Boolean, message: String)