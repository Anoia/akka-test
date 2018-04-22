package com.hero.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.hero.actors.UserSupervisor.GetUserActorRef

object UserSupervisor {

  case class GetUserActorRef(userId: String)

}

class UserSupervisor extends Actor with ActorLogging {

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"starting")
  }

  override def receive: Receive = {
    case GetUserActorRef(userId) => sender ! getOrCreateChild(userId)
  }

  def getOrCreateChild(userId: String): ActorRef = {
    context.child(userId) match {
      case Some(ref) => ref
      case None => createChild(userId)
    }
  }

  def createChild(userId: String): ActorRef = {
    context.actorOf(Props(classOf[UserActor], userId), userId)
  }
}
