package com.crawlme.crawlexservice

import java.net.URL
import java.io.InputStreamReader
import java.io.BufferedReader

case class Site(url: String = "", canonicalUrl: String)
case class Bulk(canonicals: List[String])
case class ResultObj(url: String, data: String)
case class Error(error: String)
case class FinalResult(result:List[ResultObj], error:Option[Error])

object StandaloneFetcher extends App {
	val urls = List("https://google.com", "https://github.com")
	
	override def receive = {
		case r: Site => {
			getContents(r.canonicalUrl) match {
				case Right(content) => sender ! content
				case Left(error) => sender ! akka.actor.Status.Failure(error)
			}
		}
		
		case r: Bulk => {
			//dont throw raw data to front end,, need to send as json with result = list(url,data)
			r.flatMap(self ! Site(_))
		}
	}

	def getContents(canonicalUrl:String): Either[Exception, String] = {
		val inputstream = new URL(canonicalUrl.trim).openConnection.getInputStream	
		val br = new BufferedReader(new InputStreamReader(inputStream))
		Future(fetchMeString(br, "")) map {
			case x => Right(x) 
		} recoverWith {
			case ex: Exception => println("!!! caught something here"); Left(ex)
		}
	}

	def fetchMeString(br:BufferedReader, targetString: String = ""): String = Option(br.readLine) match {
		case Some(currentLine) => fetchMeString(br, targetString + currentLine)
		case _ => targetString
	}
}