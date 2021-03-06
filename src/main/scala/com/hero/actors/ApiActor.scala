package com.hero.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import com.hero.Stuff.{AddGiftRequest, Player, SetName, UserAction}
import com.hero.actors.ApiActor.{HelloUser, UserRequest}
import com.hero.actors.UserActor.Hello
import com.hero.actors.UserSupervisor.GetUserActorRef

import scala.concurrent.Future
import scala.concurrent.duration._

object ApiActor {
  case class HelloUser(userId: String, msg: String)

  case class UserRequest(userId: String, userAction: UserAction)

}

class ApiActor(userSupervisor: ActorRef) extends Actor with ActorLogging {
  implicit val timeout: Timeout = Timeout(5 seconds)

  import context.dispatcher

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"starting")
  }

  override def receive: Receive = {
    case HelloUser(userId, msg) =>
      for (
        ref <- (userSupervisor ? GetUserActorRef(userId)).mapTo[ActorRef]
      ) yield ref ! Hello(msg)

    case UserRequest(userId, action) =>
      val replyTo = sender

      for {
        ref <- (userSupervisor ? GetUserActorRef(userId)).mapTo[ActorRef]
        result <- (ref ? action).mapTo[Player]
      } yield {
        replyTo ! result
        ref ! AddGiftRequest("firstgift")
        ref ! AddGiftRequest("secondgift")
        ref ! Hello("test2")
        ref ! AddGiftRequest("thirdgift")
        ref ! AddGiftRequest("4thgift")
        ref ! AddGiftRequest("5thggift")
      }

    case p:Player => s"got new state: $p"

    case s:String => log info s"Received message: $s"
  }


}
