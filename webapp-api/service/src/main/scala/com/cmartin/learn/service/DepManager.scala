package com.cmartin.learn.service

import akka.http.scaladsl.model.Uri
import com.cmartin.learn.repository.impl.DummyRepositoryImpl
import com.cmartin.learn.repository.spec.DummyRepository
import com.cmartin.learn.service.impl.DummyServiceImpl
import com.cmartin.learn.service.spec.{Artifact, DummyService}
import com.softwaremill.sttp._
import com.typesafe.scalalogging.Logger
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

object Main1 //extends App
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


  val responseBody = response.body match {
    //TODO manage function
    case Right(r) => r.toString
    case Left(e) => s"error: $e"
  }

  val jsonValue: JsValue = Json.parse(responseBody)
  val jsonString = Json.prettyPrint(jsonValue)

  println(s"status code ${jsonString}")


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

class DepManager

//println(s"insert into tdplibrary values('${l.g}','${l.a}','${l.v}','${l.d}','${l.s}')")
object DepManager extends App {
  lazy val logger = Logger[DepManager]
  val repository: DummyRepository = DummyRepositoryImpl()
  val service: DummyService = DummyServiceImpl(repository)
  implicit val backend = HttpURLConnectionBackend()

  // http get request to nexus

  case class Library(g: String, a: String, v: String, d: String, s: String)


  def processArtifact(v: JsValue) = {
    val artifact = Artifact(v("groupId").as[String],
      v("artifactId").as[String],
      v("version").as[String])

    logger.debug(artifact.toString)
  }

  val artifactName = "company-arch-cache"
  val repositoryName = "company-releases-lib"
  val uriString = s"http://???:8080/nexus/service/local/lucene/search?a=${artifactName}&repositoryId=${repositoryName}"
  val uri = Uri(uriString)

  logger.debug(s"nexus query request uri: $uri")

  val response = sttp.header(HeaderNames.Accept, MediaTypes.Json).get(uri"$uri").send()
  val contentType = response.header(HeaderNames.ContentType).getOrElse()
  val contentLength = response.header(HeaderNames.ContentLength).getOrElse()
  logger.debug(s"response: status=${response.code}, contentLength={${contentLength}}, contentType={${contentType}}")


  val responseBody = response.body match {
    case Right(r) => r.toString
    case Left(e) => s"error: $e"
  }
  val jsonValue: JsValue = Json.parse(responseBody)
  val jsonString = Json.prettyPrint(jsonValue)

  logger.debug(s"pretty print json: ${jsonString}")

  val result: JsResult[Option[JsArray]] = (jsonValue \ "data").validateOpt[JsArray]

  logger.trace(s"result: $result")


  result match {
    case JsSuccess(value, path) => {
      value.map(x => x.value.foreach(a => processArtifact(a)))
      //println(value.get.value.)
      //s.value.get.foreach( a => println(s"artifact: $a"))
      //case None => println("error: artifact array expected")
    }

    case _ => println("error: artifact array expected")

  }
  //  val res = (js \ "response" \ "docs").as[List[JsValue]]

}
