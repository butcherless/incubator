package com.cmartin.learn.service

import akka.http.scaladsl.model.Uri
import com.softwaremill.sttp._
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}

object Main //extends App
{
  implicit val backend = HttpURLConnectionBackend()
  val uriString = "https://search.maven.org/solrsearch/select?q=g:com.typesafe.play%20AND%20a:play-json_2.12&core=gav"
  val uri = Uri(uriString)


  println(s"akka-http-research-main: $uri")

  val request = sttp.get(uri"$uri")
  val response = request.send()
  val contentType = response.header(HeaderNames.ContentType).getOrElse()
  val contentLength = response.header(HeaderNames.ContentLength).getOrElse()
  println(s"response: status=${response.code}, contentLength={${contentType}}, contentType={${contentType}}")


  val jsonString = response.body match {
    case Right(r) => r.toString
    case Left(e) => s"error: $e"
  }

  println(s"status code ${jsonString}")

  val jsonValue: JsValue = Json.parse(jsonString)

  val element = (jsonValue \ "response" \ "numFound")

  println(s"element count: ${element.toOption.getOrElse()}")

  val artifacts = (jsonValue \ "response" \ "docs" \ "timestamp")

  println(s"artifacts: ${artifacts}")
}

object Test {

  def testFuture() = {
    import ExecutionContext.Implicits.global
    val f = Future("hello")
    val res = f.onComplete {
      case scala.util.Success(value) => "success"
      case scala.util.Failure(exception) => "failure"
    }
  }
}

object Main1 extends App {
  val jsonString =
    """
      |{"responseHeader":{"status":0,"QTime":0,"params":{"q":"g:com.typesafe.play AND a:play-json_2.12","core":"gav","indent":"off","fl":"id,g,a,v,p,ec,timestamp,tags","sort":"score desc,timestamp desc,g asc,a asc,v desc","version":"2.2","wt":"json"}},"response":{"numFound":21,"start":0,"docs":[{"id":"com.typesafe.play:play-json_2.12:2.7.0-M1","g":"com.typesafe.play","a":"play-json_2.12","v":"2.7.0-M1","p":"jar","timestamp":1534536075000,"ec":["-sources.jar","-javadoc.jar",".jar",".pom","-playdoc.jar"],"tags":["json","play"]},{"id":"com.typesafe.play:play-json_2.12:2.6.10","g":"com.typesafe.play","a":"play-json_2.12","v":"2.6.10","p":"jar","timestamp":1534534574000,"ec":["-javadoc.jar","-sources.jar",".jar",".pom","-playdoc.jar"],"tags":["json","play"]},{"id":"com.typesafe.play:play-json_2.12:2.6.9","g":"com.typesafe.play","a":"play-json_2.12","v":"2.6.9","p":"jar","timestamp":1519932315000,"ec":["-javadoc.jar","-sources.jar",".jar",".pom","-playdoc.jar"],"tags":["json","play"]},{"id":"com.typesafe.play:play-json_2.12:2.6.8","g":"com.typesafe.play","a":"play-json_2.12","v":"2.6.8","p":"jar","timestamp":1512693125000,"ec":["-javadoc.jar","-sources.jar",".jar",".pom","-playdoc.jar"],"tags":["json","play"]},{"id":"com.typesafe.play:play-json_2.12:2.6.7","g":"com.typesafe.play","a":"play-json_2.12","v":"2.6.7","p":"jar","timestamp":1509055949000,"ec":["-sources.jar","-javadoc.jar",".jar",".pom","-playdoc.jar"],"tags":["json","play"]},{"id":"com.typesafe.play:play-json_2.12:2.6.6","g":"com.typesafe.play","a":"play-json_2.12","v":"2.6.6","p":"jar","timestamp":1506383598000,"ec":["-sources.jar","-javadoc.jar",".jar",".pom","-playdoc.jar"],"tags":["json","play"]},{"id":"com.typesafe.play:play-json_2.12:2.6.5","g":"com.typesafe.play","a":"play-json_2.12","v":"2.6.5","p":"jar","timestamp":1505419292000,"ec":["-sources.jar","-javadoc.jar",".jar",".pom","-playdoc.jar"],"tags":["json","play"]},{"id":"com.typesafe.play:play-json_2.12:2.6.4","g":"com.typesafe.play","a":"play-json_2.12","v":"2.6.4","p":"jar","timestamp":1505268842000,"ec":["-sources.jar","-javadoc.jar",".jar",".pom","-playdoc.jar"],"tags":["json","play"]},{"id":"com.typesafe.play:play-json_2.12:2.6.3","g":"com.typesafe.play","a":"play-json_2.12","v":"2.6.3","p":"jar","timestamp":1502315706000,"ec":["-javadoc.jar","-sources.jar",".jar",".pom","-playdoc.jar"],"tags":["json","play"]},{"id":"com.typesafe.play:play-json_2.12:2.6.2","g":"com.typesafe.play","a":"play-json_2.12","v":"2.6.2","p":"jar","timestamp":1499285377000,"ec":["-javadoc.jar","-sources.jar",".jar",".pom","-playdoc.jar"],"tags":["json","play"]}]}}
    """.stripMargin

  val js = Json.parse(jsonString)

  //val res = (js \ "response" \ "docs" \\ "id")
  val res = (js \ "response" \ "docs").as[List[JsValue]]

  println(res)

}