package com.cmartin.learn.service

import com.cmartin.learn.common.sayHello
import com.cmartin.learn.repository.spec.DummyRepository
import com.cmartin.learn.service.spec.{Artifact, DummyService, GAV, Library}
import com.softwaremill.sttp._
import play.api.libs.json._

//import com.softwaremill.sttp.{HeaderNames, HttpURLConnectionBackend, Uri,sttp, MediaTypes}
import com.typesafe.scalalogging.Logger

import scala.util.Try

package object impl {

  //  implicit val system = ActorSystem()
  // implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  //  implicit val executionContext = system.dispatcher


  //val uri = Uri("https://search.maven.org/solrsearch/select?q=g:com.typesafe.play%20AND%20a:play-json_2.12&core=gav")

  class DummyServiceImpl(settings: Settings, repository: DummyRepository) extends DummyService {

    val logger = Logger[DummyServiceImpl]
    implicit val backend = HttpURLConnectionBackend()
    val nexusEndpoint = s"http://${settings.host}:${settings.port}/nexus/service/local/lucene/search"


    override def operationOne(): String = {
      repository.saveDummy()
      sayHello()
    }

    override def searchKey(json: String, key: String): Unit = {
      // Option[String] = {
      /*
            val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = uri2))
            Await.result(responseFuture, Duration(5000, TimeUnit.MILLISECONDS));

            val res = responseFuture.onComplete {
              case Success(res) => println(s"success: ${res.entity.dataBytes}")
              case Failure(_) => println("failure")
            }

      */
    }

    // TODO refactor operations with Try[T]
    override def getArtifactVersions(name: String, repo: String): Try[List[Artifact]] = {
      val endpoint: Uri = uri"${nexusEndpoint}?a=${name}&repositoryId=${repo}"
      logger.trace(s"uri: ${endpoint}")

      // send request to the nexus server
      val response = sttp.header(HeaderNames.Accept, MediaTypes.Json).get(endpoint).send()
      traceResponse(response)

      // retrieve json from the body response
      val jsonString: String = response.body match {
        case Right(r) => r.toString
        case Left(e) => s"""{"error": "${e.toString}"}"""
      }

      // parse the json string from the response
      val jsonValue: JsValue = Json.parse(jsonString)
      logger.trace(Json.prettyPrint(jsonValue))

      val data: JsResult[Option[JsArray]] = (jsonValue \ "data").validateOpt[JsArray]

      val result: Seq[Artifact] = data match {
        case JsSuccess(value, path) => {
          value.map(x => x.value.map(a => processArtifact(a))).getOrElse(Seq.empty[Artifact])
        }
        case _ => Seq.empty[Artifact] //println("error: artifact array expected")
      }

      Try(result.toList)
    }

    override def getArtifactFiles(gav: GAV, repo: String): Try[List[Library]] = ???

    /*
     _    _ ______ _      _____  ______ _____
    | |  | |  ____| |    |  __ \|  ____|  __ \
    | |__| | |__  | |    | |__) | |__  | |__) |
    |  __  |  __| | |    |  ___/|  __| |  _  /
    | |  | | |____| |____| |    | |____| | \ \
    |_|  |_|______|______|_|    |______|_|  \_\
     */

    private def processArtifact(v: JsValue) = {
      val artifact = Artifact(v("groupId").as[String],
        v("artifactId").as[String],
        v("version").as[String])

      logger.trace(artifact.toString)
      artifact
    }

    private def traceResponse(response: Id[Response[String]]) = {
      val contentType = response.header(HeaderNames.ContentType).getOrElse()
      val contentLength = response.header(HeaderNames.ContentLength).getOrElse()

      logger.trace(s"response: status=${response.code}, contentLength=${contentLength}, contentType=${contentType}")
    }

  }

  object DummyServiceImpl {
    def apply(settings: Settings, repository: DummyRepository): DummyServiceImpl = new DummyServiceImpl(settings, repository)
  }

}

