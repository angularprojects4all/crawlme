package com.crawlme.crawlexformats

object Models {

case class Result(url: String, data: String)
case class Error(error: String)

case class FinalResult(result: List[Result], error: Option[Error])

}