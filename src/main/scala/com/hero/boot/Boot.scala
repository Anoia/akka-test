package com.hero.boot

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.hero.actors.ApiActor
import com.hero.route.HelloUserRoute

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

object Boot extends App {
  var b: Boot = _

  override def main(args: Array[String]): Unit = {
    b = new Boot()
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    b.stop()
  }
}

class Boot() extends HelloUserRoute {
  implicit val timeout: Timeout = Timeout(5 seconds)
  implicit val system: ActorSystem = ActorSystem("AkkaTestSystem")
  // default Actor constructor
  override val apiActor: ActorRef = system.actorOf(Props[ApiActor], name = "Api")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(helloUserRoute, "localhost", 8080)

  def stop(): Unit = {
    println("shutting down..")
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}

