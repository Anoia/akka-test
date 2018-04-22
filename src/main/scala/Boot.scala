import akka.actor.{Actor, ActorSystem, Props}

object Boot extends App {

  override def main(args: Array[String]): Unit = {
    val system = ActorSystem("HelloSystem")
    // default Actor constructor
    val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")
    helloActor ! "hello"
    helloActor ! "buenos dias"
  }

  class HelloActor extends Actor {
    def receive = {
      case "hello" => println("hello back at you")
      case _ => println("huh?")
    }
  }

}

