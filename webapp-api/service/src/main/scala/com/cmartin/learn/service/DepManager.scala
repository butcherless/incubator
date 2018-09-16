package com.cmartin.learn.service

import com.cmartin.learn.repository.impl.DummyRepositoryImpl
import com.cmartin.learn.repository.spec.DummyRepository
import com.cmartin.learn.service.impl.DummyServiceImpl
import com.cmartin.learn.service.spec.DummyService
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


object Test {

  def testFuture() = {
    import ExecutionContext.Implicits.global
    val f = Future("hello")
    val res = f.onComplete {
      case scala.util.Success(value) => "success"
      case Failure(exception) => "failure"
    }
  }
}

class DepManager

case class Settings(host: String, port: Int, repo: String)

//println(s"insert into tdplibrary values('${l.g}','${l.a}','${l.v}','${l.d}','${l.s}')")
object DepManager extends App {
  lazy val logger = Logger[DepManager]
  val repository: DummyRepository = DummyRepositoryImpl()

  // http get request to nexus
  val artifactName = "mma-arch-cache"
  val repositoryName = "mutua-releases-lib"

  def init(): Settings = {
    val config: Config = ConfigFactory.load()
    val settings = Settings(
      config.getString("nexus.host"),
      config.getInt("nexus.port"),
      config.getString("nexus.repo")
    )
    logger.info(s"settings: {host=${settings.host}, port=${settings.port}, repository=${settings.repo}}")
    settings
  }

  val settings = init()
  val service: DummyService = DummyServiceImpl(settings, repository)

  val artifacts = service.getArtifactVersions(artifactName, repositoryName)

  artifacts match {
    case Success(list) => list.foreach(a => logger.debug(a.toString()))
    case Failure(ex) => logger.error(ex.getMessage)
  }

  logger.info("application stopped.")
}


/*

  def processArtifact(v: JsValue) = {
    val artifact = Artifact(v("groupId").as[String],
      v("artifactId").as[String],
      v("version").as[String])

    logger.debug(artifact.toString)
  }

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
*/

