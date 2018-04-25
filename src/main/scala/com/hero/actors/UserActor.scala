package com.hero.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, ReceiveTimeout, Stash}
import com.hero.Stuff.{ActionWithPlayer, Player, Session, UserAction}
import com.hero.actors.UserActor.{Hello, Initialize}

import scala.concurrent.Future
import scala.concurrent.duration._

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
                sender ! "done"
    case action: UserAction =>
      //tell some actor to do something, expect user result
      log info s"handling new userAction:$action"
      game ! ActionWithPlayer(session.player, action)
      context.setReceiveTimeout(1 seconds)
      context.become(waitForResponseBehavior(session, sender))
    case result: Player => log error s"received result in handleNewMessageBehavior? $result"
    case m => log error s"unexpected message: $m"

  }

  def waitForResponseBehavior(session: Session, replyTo: ActorRef): Receive = {
    case p: Player =>
      log info s"received user result: $p"
      context.setReceiveTimeout(Duration.Undefined)
      val updatedSession = session.copy(player = p)
      log info "updated session, unstashing all messages"
      unstashAll()
      context.become(handleNewMessageBehavior(updatedSession))
      replyTo ! updatedSession.player
    case s:String =>
      log info s"Received message: $s"
      replyTo ! s
    case _: ReceiveTimeout =>
      log error "response did not arrive on time! either takes to long or crashed, at least sth unexpected. return generic error to app"
      context.setReceiveTimeout(Duration.Undefined)
      unstashAll()
      context.become(handleNewMessageBehavior(session))
      replyTo ! "error"
    case m =>
      log info s"stashing message while processing a different one: $m"
      stash()
  }
}
