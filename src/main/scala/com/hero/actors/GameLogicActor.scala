package com.hero.actors

import akka.actor.{Actor, ActorLogging}
import com.hero.Stuff.{ActionWithPlayer, AddGift, Player, SetName}

class GameLogicActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case ActionWithPlayer(player, action: SetName) => sender ! setName(player, action)
    case ActionWithPlayer(player, action: AddGift) => sender ! addGift(player, action)
  }

  def setName(player: Player, action: SetName): Player = {
    Thread sleep 500
    log info s"setting name to ${action.newName}"
    player.copy(name = action.newName)
  }

  def addGift(player: Player, action: AddGift): Player = {
    Thread sleep 500
    log info s"adding gift ${action.gift}"
    player.copy(gifts = action.gift :: player.gifts)
  }
}
