package com.amitbansal7

import org.mongodb.scala.model.Filters._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object userMongoRepository {

  val userCollection = MongoConfig.getUserCollection

  import com.mongodb.async.SingleResultCallback

  val callbackWhenFinished: SingleResultCallback[Void] = new SingleResultCallback[Void]() {
    override def onResult(result: Void, t: Throwable): Unit = {
      System.out.println("Operation Finished!")
    }
  }

  def insertUser(user: User) =
    Await.result(userCollection.insertOne(user).toFuture(), 1 seconds)

  def getOneByEmail(email: String) =
    Await.result(userCollection.find(equal("email", email)).first().toFuture(), 1 seconds)

  def getAll() =
    userCollection.find().collect()

  def deleteOneByEmail(email: String) =
    Await.result(userCollection.deleteOne(equal("email", email)).toFuture(), 1 seconds)

  def getByUserName(username: String): User =
    Await.result(userCollection.find(equal("username", username)).first().toFuture(), 1 seconds)

  def getByEmail(email: String): User =
    Await.result(userCollection.find(equal("email", email)).first().toFuture(), 1 seconds)

  def existByUsername(username: String): Boolean = {
    getByUserName(username) != null
  }

  def existByEmail(email: String): Boolean = {
    getByEmail(email) != null
  }

}
