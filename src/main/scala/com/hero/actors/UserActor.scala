package com.hero.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash}
import com.hero.Stuff.{ActionWithPlayer, Player, Session, UserAction}
import com.hero.actors.UserActor.{Hello, Initialize}

import scala.concurrent.Future

object UserActor {

  case class Hello(mgs: String)

  case class Initialize(session: Session)

  case class UserResult(player: Player)

}

class UserActor(userId: String) extends Actor with ActorLogging with Stash {

  import context.dispatcher

  val game: ActorRef = context.actorOf(Props[GameLogicActor], "game")

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"starting")

    for {
      player <- loadPlayer()
      config <- loadConfig()
    }
      yield self ! Initialize(Session(player, config))
  }

  def loadPlayer(): Future[Player] = {
    log info "loading player"
    Thread.sleep(1000)
    Future(Player("defaultName", 1))
  }

  def loadConfig(): Future[String] = {
    log info "loading config"
    Thread.sleep(1000)
    Future("this is the config")
  }

  override def receive: Receive = {
    case Initialize(session) =>
      log info "initializing"
      unstashAll()
      context become handleNewMessageBehavior(session)
    case m =>
      log info s"stashing message in default behavior: $m"
      stash()
  }

  def handleNewMessageBehavior(session: Session): Receive = {
    case Hello(msg) => log.info(s"HelloUser:$msg")
    case action: UserAction =>
      //tell some actor to do something, expect user result
      game ! ActionWithPlayer(session.player, action)
      context.become(waitForResponseBehavior(session, sender))
    case result: Player => log error s"received result in handleNewMessageBehavior? $result"
    case _ => log error "unexpected message"

  }

  def waitForResponseBehavior(session: Session, replyTo: ActorRef): Receive = {
    case p: Player =>
      log info "received user result"
      val updatedSession = session.copy(player = p)
      unstashAll()
      context.become(handleNewMessageBehavior(updatedSession))
      replyTo ! updatedSession.player
    case m =>
      log info s"stashing message while processing a different one: $m"
      stash()
  }
}
