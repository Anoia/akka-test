package com.hero.route

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.hero.actors.ApiActor.HelloUser

trait HelloUserRoute {

  val apiActor: ActorRef

  val helloUserRoute: Route =
    path("hello") {
      put {
        parameter("user") {
          user =>
            println(user)
            apiActor ! HelloUser(user, "hello")
            complete((StatusCodes.Accepted, s"said hello to $user"))
        }
      }
    }

}
