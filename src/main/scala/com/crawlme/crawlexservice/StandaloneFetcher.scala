package com.crawlme.crawlexservice

import java.net.URL
import java.io.InputStreamReader
import java.io.BufferedReader
import akka.actor.Actor
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent._
import com.crawlme.crawlexformats.Models._

case class Site(canonicalUrl: String)
case class Bulk(canonicals: List[String])

class StandaloneFetcher extends Actor {
	
	override def receive = {
		case r: Site => {
			getContents(r.canonicalUrl) match {
				case Right(content) => sender ! FinalResult(List(Result(r.canonicalUrl,content)), None)
				case Left(error) => sender ! akka.actor.Status.Failure(error)
			}
		}
		
		case r: Bulk => {
			//dont throw raw data to front end,, need to send as json with result = list(url,data)
			r.canonicals.foreach(self ! Site(_))
		}
	}

	def firstReadFromRedis(cUrl: String) = ???

	def getContents(canonicalUrl:String, isTrailUser: Boolean = true): Either[Exception, String] = {
		val inputStream = new URL(canonicalUrl.trim).openConnection.getInputStream	
		val br = new BufferedReader(new InputStreamReader(inputStream))
		Right(fetchMeString(br, isTrailUser))
	}	

	def fetchMeString(br:BufferedReader, isTrailUser: Boolean = true, targetString: String = ""): String =  
				Option(br.readLine) match {
					case Some(currentLine) => fetchMeString(br, isTrailUser, targetString + currentLine)
					case _ => targetString
	}
}