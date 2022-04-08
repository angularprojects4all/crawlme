package com.dbs.bikerental

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
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,"<html><body>Hello world!</body></html>"))
        } ~
          path("getAllStations") {
            complete("""
[
 {"stationName":"Kukatpally",
  "lattitude":"17.4875",
  "longitude":"78.3953",
  "id": 1,
  "bikesAvailable":15
 },
 {"stationName":"Jubilee Hills",
  "lattitude":"17.4326",
  "longitude":"78.4071",
  "id": 2,
  "bikesAvailable":20
 },
 {"stationName":"Banjara Hills",
  "lattitude":"17.4169",
  "longitude":"78.4387",
  "id": 3,
  "bikesAvailable":19
 },
 {"stationName":"SR Nagar",
  "lattitude":"17.4172",
  "longitude":"78.4387",
  "id": 4,
  "bikesAvailable":19
 },
{"stationName":"Manikonda",
  "lattitude":"17.4172",
  "longitude":"78.4387",
  "id": 5,
  "bikesAvailable":0
 }
]
""")
          } ~
          path(p"statsummary/city/1") {
		complete("""
[
{"date":"26-03-2022 12:00",
"stationWise": {
 [
  {"stationId":1,
  "stationAvgTemp":28.80,
  "stationOpeningTemp":25.80,
  "stationClosingTemp":32.20
},
  {"stationId":2,
  "stationAvgTemp":26.18,
  "stationOpeningTemp":24.90,
  "stationClosingTemp":33.20
},
  {"stationId":3,
  "stationAvgTemp":26.18,
  "stationOpeningTemp":24.90,
  "stationClosingTemp":33.20
}
]
}
"cityWise": {"avgTemp":29.00, "highest":33.00,"lowest":26.00}
}

]

""")
          }
      }

    // `route` will be implicitly converted to `Flow` using `RouteResult.route2HandlerFlow`
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8087)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    if(StdIn.readLine() == "quit") // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}