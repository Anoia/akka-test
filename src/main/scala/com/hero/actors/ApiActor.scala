package com.hero.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.hero.actors.ApiActor.HelloUser
import com.hero.actors.UserActor.Hello
import com.hero.actors.UserSupervisor.GetUserActorRef

import scala.concurrent.duration._

object ApiActor {

  case class HelloUser(userId: String, msg: String)

}

class ApiActor() extends Actor with ActorLogging {
  implicit val timeout: Timeout = Timeout(5 seconds)

  import context.dispatcher

  val userSupervisor: ActorRef = context.actorOf(Props[UserSupervisor], name = "UserSupervisor")

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"starting")
  }

  override def receive: Receive = {
    case HelloUser(userId: String, msg: String) =>
      for (
        ref <- (userSupervisor ? GetUserActorRef(userId)).mapTo[ActorRef]
      ) yield ref ! Hello(msg)
  }


}
