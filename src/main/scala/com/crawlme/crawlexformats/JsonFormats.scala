package com.crawlme.crawlexformats
import spray.json.DefaultJsonProtocol
import com.crawlme.crawlexformats.Models._

object JsonFormats  {
  import DefaultJsonProtocol._

  implicit val resultJsonFormat = jsonFormat2(Result)
  implicit val errorJsonFormat = jsonFormat1(Error)
  implicit val finalResultJsonFormat = jsonFormat2(FinalResult)

//  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
