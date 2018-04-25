package com.hero.actors

import akka.actor.{Actor, ActorLogging}
import com.hero.Stuff.{ActionWithPlayer, AddGiftRequest, Player, SetName}

class GameLogicActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case ActionWithPlayer(player, action: SetName) => sender ! setName(player, action)

    case ActionWithPlayer(player, action: AddGiftRequest) => sender ! addGift(player, action)

    case ActionWithPlayer(player, action) => action match {
      case m:SetName => sender ! setName(player, m)
      case m:AddGiftRequest =>  sender ! addGift(player, m)
    }

    case(p:Player, action:AddGiftRequest) => sender ! addGift(p, action)
  }

  def setName(player: Player, action: SetName): Player = {
    Thread sleep 500
    log info s"setting name to ${action.newName}"
    player.copy(name = action.newName)
  }

  def addGift(player: Player, action: AddGiftRequest): Player = {
    Thread sleep 500
    log info s"adding gift ${action.gift}"
    player.copy(gifts = action.gift :: player.gifts)
  }
}
