package com.crawlme.crawlexroutes

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn



object WebServer {
  def main(args: Array[String]) {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route =
      get {
        pathSingleSlash {
          complete("HELOO WORLD")
        } ~
          path("api/crawl") {
            complete("you just tried crawling sites here.. TBD")
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