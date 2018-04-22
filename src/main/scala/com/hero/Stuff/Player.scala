package com.hero.Stuff

case class Player(name: String, level: Int, gifts: List[String] = Nil)

case class Session(player: Player, config: String)
