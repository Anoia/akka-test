package com.hero.Stuff

trait UserAction

case class SetName(newName: String) extends UserAction

case class AddGift(gift: String) extends UserAction

case class ActionWithPlayer(player: Player, action: UserAction)



