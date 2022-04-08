package com.crawlme.crawlexactors
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem

case object Ping
class Crawler extends Actor {
  import context._

  def receive = {
    case Ping => sender ! "pong"
    case _ => sender ! "excuse me!"
  }
}


/*
object Crawler extends App {
  val system = ActorSystem("SiteCrawlSystem")
  val crawlTest = system.actorOf(Props[Starter](), name = "cx")
  swap ! Ping
}
*/