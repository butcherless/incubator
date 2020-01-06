package com.cmartin.learn.app

import com.cmartin.learn.logic._
import com.cmartin.learn.service.impl.{JsonNexusRepository, getNexusSettings}
import com.cmartin.learn.service.spec._
import com.typesafe.scalalogging.Logger

import scala.util.{Success, Try}

class MainApp

object MainApp
//  extends App
{
  lazy val logger      = Logger[MainApp]
  lazy val initInstant = getTimestamp()
  lazy val repository  = JsonNexusRepository(getNexusSettings())

  //implicit val repoName = "mutua-releases-lib"
  implicit val repoName = "mutua-snapshots"
  //val artifactName = "mma-arch-base"
  val artifactName = "scs-multicanal-perfilado"

  logger.info(s"MainApp starts at: ${initInstant} instant")

  val temp: Try[List[Library]] = repository
    .getVersions(artifactName, repoName)
    .map(
      x =>
        x.flatMap(
          g =>
            repository
              .getGavFiles(g, repoName)
              .getOrElse(List.empty)
        )
    )

  val files: Try[List[Library]] = for {
    versions <- repository.getVersions(artifactName, repoName)
    files    <- repository.getGavFiles(versions)
  } yield files

  val result: Unit = files match {
    case Success(list)           => processResults(list)
    case util.Failure(exception) => logger.error(s"error while processing results: $exception")
  }

  logger.info(s"MainApp stops after: ${getTime(initInstant)} ms")
}

/*
  val gav: Future[Option[GAV]] = getGav(1)

  val result: Option[GAV] = Await.result(gav, 2.second)


  val coordinates = repository.getVersions("mma-arch-base", repoName)

  val r1 = coordinates.getOrElse(List.empty[GAV])

  val r2 = r1.map(repository.getAsyncGavFiles(_, repoName))

  val r3: Future[List[List[Library]]] = Future.traverse(r2) { f => f }

 */
