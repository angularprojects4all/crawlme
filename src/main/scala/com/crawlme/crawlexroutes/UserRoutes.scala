package com.crawlme.crawlexroutes

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import akka.actor._
import akka.actor.Props
import com.crawlme.crawlexservice.StandaloneFetcher
import com.crawlme.crawlexservice._
import akka.pattern.ask
import scala.concurrent.ExecutionContext.Implicits
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent._



object WebServer {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("teamextn")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher
    implicit val timeout = Timeout(10.seconds)

    val fetcherActor = system.actorOf(Props[StandaloneFetcher], "fetcher")
    val urls = List("https://google.com", "https://github.com")

    val route =
      get {
        pathSingleSlash {
          complete("HELOO WORLD")
        } ~
          path("api" / "crawl") {
            complete((fetcherActor ? Site(urls.tail.head)).mapTo[String])
		//need to remove nasty awaits, self-note:TODO
          }
      }
	
    // `route` will be implicitly converted to `Flow` using `RouteResult.route2HandlerFlow`
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8087)
    println(s"Server online at http://localhost:8087/\nPress RETURN to stop...")
    if(StdIn.readLine() == "quit") // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}