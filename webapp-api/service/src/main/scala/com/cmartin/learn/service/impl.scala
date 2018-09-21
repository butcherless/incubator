package com.cmartin.learn.service

import com.cmartin.learn.service.spec._
import com.softwaremill.sttp._
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import org.json4s.native.JsonMethods.parse
import org.json4s.{DefaultFormats, JValue}

import scala.util.{Failure, Success, Try}

package object impl {

  case class NexusSettings(host: String, port: Int, repo: String) {
    def getSearchUri() = s"http://${host}:${port}/nexus/service/local/lucene/search"

    def getGavFilesUri() = s"http://${host}:${port}/nexus/service/local/repositories"
  }

  class JsonNexusRepository(settings: NexusSettings) extends NexusRepository {
    val logger: Logger = Logger[JsonNexusRepository]
    implicit val backend = HttpURLConnectionBackend()
    implicit lazy val formats = DefaultFormats


    override def getVersions(artifactName: String, repositoryName: String): Try[List[GAV]] = {
      for {
        endpoint <- buildEndpoint(artifactName, repositoryName)
        response <- getJsonResponse(endpoint)
        jsonString <- getJsonString(response)
        coordinates <- getCoordinates(jsonString)
        _ <- traceResponse(response)
        _ <- traceCoordinates(coordinates)
      } yield coordinates
    }

    override def getGavFiles(gav: GAV, repositoryName: String): Try[List[Library]] = {
      for {
        endpoint <- buildGavFilesEndpoint(gav, repositoryName)
        response <- getJsonResponse(endpoint)
        jsonString <- getJsonString(response)
        files <- getFiles(gav, jsonString)
      } yield files
    }

    /*
     _    _   ______   _        _____    ______   _____     _____
    | |  | | |  ____| | |      |  __ \  |  ____| |  __ \   / ____|
    | |__| | | |__    | |      | |__) | | |__    | |__) | | (___
    |  __  | |  __|   | |      |  ___/  |  __|   |  _  /   \___ \
    | |  | | | |____  | |____  | |      | |____  | | \ \   ____) |
    |_|  |_| |______| |______| |_|      |______| |_|  \_\ |_____/
    */


    private def buildEndpoint(artifactName: String, repositoryName: String): Try[Uri] = {
      val endpoint: Uri = uri"${settings.getSearchUri()}?a=${artifactName}&repositoryId=${repositoryName}"
      this.logger.trace(s"uri: ${endpoint}")

      Try(endpoint)
    }

    private def buildGavFilesEndpoint(gav: GAV, repositoryName: String): Try[Uri] = {
      val groupIdPath = gav.groupId.replace('.', '/')
      val endpoint: Uri = uri"${settings.getGavFilesUri()}/${repositoryName}/content/${groupIdPath}/${gav.artifactId}/${gav.version}/"
      this.logger.trace(s"uri: ${endpoint}")

      Try(endpoint)
    }

    // send request to the nexus server
    private def getJsonResponse(endpoint: Uri) = {
      Try(sttp.header(HeaderNames.Accept, MediaTypes.Json).get(endpoint).send())
    }

    /* retrieve json from the body response */
    private def getJsonString(response: Response[String]) = {
      response.body match {
        case Right(r) => Success(r.toString)
        case Left(e) => Failure(new RuntimeException(s"${e.toString}"))
      }
    }

    private def getFiles(gav: GAV, jsonString: String): Try[List[Library]] = {
      Try(
        (parse(jsonString) \\ "data")
          .children
          .map(extractLibrary(gav, _))
      )
    }

    private def getCoordinates(jsonString: String): Try[List[GAV]] = {
      Try(
        (parse(jsonString) \\ "data")
          .children
          .map(extractArtifact(_))
      )
    }

    private def extractLibrary(gav: GAV, value: JValue): Library = {
      Library(gav.groupId, gav.artifactId, gav.version, (value \ "lastModified").extract[String], (value \ "text").extract[String])
    }

    private def extractArtifact(value: JValue): GAV = {
      GAV((value \ "groupId").extract[String], (value \ "artifactId").extract[String], (value \ "version").extract[String])
    }

    private def traceResponse(response: Response[String]): Try[Unit] = {
      val contentType = response.header(HeaderNames.ContentType).getOrElse()
      val contentLength = response.header(HeaderNames.ContentLength).getOrElse()

      Try(logger.trace(s"response={status=${response.code}, contentLength=${contentLength}, contentType=${contentType}}"))
    }

    private def traceCoordinates(coordinates: List[GAV]): Try[Unit] = {
      Try(logger.trace(s"retrieved ${coordinates.size} artifacts, printing up to 5 elements: ${coordinates.take(5).toString()}"))
    }

  }

  object JsonNexusRepository {
    def apply(settings: NexusSettings): JsonNexusRepository = new JsonNexusRepository(settings)
  }

  def getNexusSettings(): NexusSettings = {
    val config: Config = ConfigFactory.load()
    NexusSettings(
      config.getString("nexus.host"),
      config.getInt("nexus.port"),
      config.getString("nexus.repo")
    )
  }


  /*
    _        ______    _____               _____  __     __       _____    ____    _____    ______
   | |      |  ____|  / ____|     /\      / ____| \ \   / /      / ____|  / __ \  |  __ \  |  ____|
   | |      | |__    | |  __     /  \    | |       \ \_/ /      | |      | |  | | | |  | | | |__
   | |      |  __|   | | |_ |   / /\ \   | |        \   /       | |      | |  | | | |  | | |  __|
   | |____  | |____  | |__| |  / ____ \  | |____     | |        | |____  | |__| | | |__| | | |____
   |______| |______|  \_____| /_/    \_\  \_____|    |_|         \_____|  \____/  |_____/  |______|


   */


  //  implicit val system = ActorSystem()
  // implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  //  implicit val executionContext = system.dispatcher

  //val uri = Uri("https://search.maven.org/solrsearch/select?q=g:com.typesafe.play%20AND%20a:play-json_2.12&core=gav")

}

