package com.amitbansal7

import akka.actor.{ Actor, ActorLogging, Props }
import org.mongodb.scala.{ Completed, Observer }
import org.apache.commons.codec.digest.DigestUtils
import scala.concurrent.duration._
import scala.concurrent.Await

object UserActor {
  def props: Props =
    Props(classOf[UserActor])
}

class UserActor extends Actor with ActorLogging {

  def receive = {
    case AddUser(user: User) =>

      if (userMongoRepository.existByUsername(user.username)) {
        sender() ! "Username already exists"
      } else if (userMongoRepository.existByEmail(user.email)) {
        sender() ! "Email already exists"
      } else {
        sender() ! "User successfully added."
        userMongoRepository.insertUser(user)
      }

    case GetAllUsers =>
      val fu = userMongoRepository.getAll()
      val res = Await.result(fu.toFuture(), 1 seconds)
      sender() ! UserResponse(res.toList)

    case Authenticate(username, email, password) =>

      def getUser(username: Option[String], email: Option[String]): User = username match {
        case None => userMongoRepository.getByEmail(email.get)
        case Some(u) => userMongoRepository.getByUserName(u)
      }

      val user = getUser(username, email)
      if (user == null) {
        sender() ! AuthRes(false, "No user found")
      } else {
        if (user.password == User.getPasshash(password)) {
          sender() ! AuthRes(true, "User is authenticated")
        } else {
          sender() ! AuthRes(false, "Incorrect password")
        }
      }

    case DeleteOne(email) =>
      userMongoRepository.deleteOneByEmail(email)
      sender() ! "User is deleted."
  }
}
