package com.hero.route

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.hero.Stuff.{Player, SetName}
import com.hero.actors.ApiActor.{HelloUser, UserRequest}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.Future

trait HelloUserRoute {
  implicit val timeout: Timeout
  implicit val materializer: ActorMaterializer

  implicit val playerFormat: RootJsonFormat[Player] = jsonFormat3(Player)
  val apiActor: ActorRef

  val helloUserRoute: Route =
    path("hello") {
      put {
        parameter("user") {
          user =>
            apiActor ! HelloUser(user, "hello")
            complete((StatusCodes.Accepted, "stest"))
        }
      }
    }


  val handleRequestRoute: Route =
    path("user") {
      put {
        parameter("id", "name") {
          (id, name) =>
            val r: Future[Player] = (apiActor ? UserRequest(id, SetName(name))).mapTo[Player]
            complete(r)

        }
      }
    }


}



