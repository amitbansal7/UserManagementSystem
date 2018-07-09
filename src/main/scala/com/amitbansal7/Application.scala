package com.amitbansal7

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.http.scaladsl.server.Directives._

import scala.concurrent.duration._
import scala.io.StdIn
import akka.pattern.ask
import JsonSupport._
import scala.util.{Success, Failure}

object Application {

  val host = "localhost"
  val port = 8090

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("user-management-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    implicit val timeout = Timeout(20 seconds)

    val userActor = system.actorOf(UserActor.props, "user-actor")

    val route: Route = {
      (path("health") & get) {
        complete(StatusCodes.OK, "Server is up and running..")
      } ~
        pathPrefix("users") {
          (path("add") & post) {
            parameter(
              'username,
              'email,
              'password,
              'firstName.?,
              'lastName.?,
              'birthDate.?
            ) {
              (username, email, password, firstName, lastName, birthDate) =>
                onSuccess(userActor ? AddUser(User(username, email, password, firstName, lastName, birthDate))) {
                  case s: String => complete(StatusCodes.OK, s)
                }
            }
          } ~
            (path("getall") & get) {
              onSuccess(userActor ? GetAllUsers) {
                case UserResponse(users) =>
                  complete(StatusCodes.OK, users)
                case s: String =>
                  complete(StatusCodes.OK, s)
              }
            } ~
            (path("authenticate") & get) {
              parameter('username.?, 'email.?, 'password) { (username, email, password) =>
                if (username == None && email == None) {
                  complete(StatusCodes.BadRequest, "Provide either username or email")
                } else {
                  onSuccess(userActor ? Authenticate(username, email, password)) {
                    case res: AuthRes => complete(StatusCodes.OK, res)
                    case _ => complete(StatusCodes.InternalServerError)
                  }
                }
              }
            } ~
            (path("delete") & post) {
              parameter('email) { email =>
                onSuccess(userActor ? DeleteOne(email)){
                  case s:String => complete(StatusCodes.OK, s)
                }
              }
            }
        }
    }

    val bindingFuture = Http().bindAndHandle(route, host, port)

    bindingFuture.onComplete {
      case Success(_) => println(s"Server is running at ${host}:${port}\nHit return to terminate..")
      case Failure(e) => println(s"could not start application: {}", e.getMessage)
    }

    StdIn.readLine()
    bindingFuture.flatMap(_.unbind())
    system.terminate()
    println("Server is closed.")

  }
}
