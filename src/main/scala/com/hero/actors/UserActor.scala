package com.hero.actors

import akka.actor.{Actor, ActorLogging}
import com.hero.actors.UserActor.Hello

object UserActor {

  case class Hello(mgs: String)

}

class UserActor(userId: String) extends Actor with ActorLogging {

  val handleNewMessageBehavior: Receive = {
    case Hello(msg) =>
      log.info(s"$userId:HelloUser:$msg")
  }

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"starting")
  }

  override def receive: Receive = handleNewMessageBehavior
}
