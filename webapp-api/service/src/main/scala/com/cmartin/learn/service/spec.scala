package com.cmartin.learn.service

import scala.concurrent.Future
import scala.util.Try

package object spec {

  type OptionLibrary = Option[Library]

  case class Artifact(g: String, a: String, v: String)

  case class Library(g: String, a: String, v: String, d: String, s: String)

  case class GAV(groupId: String, artifactId: String, version: String)

  trait DummyService {
    def operationOne(): String

    def searchKey(json: String, key: String): Unit // Option[String]

    def getArtifactVersions(name: String, repo: String): Try[List[Artifact]]

    def getArtifactFiles(gav: GAV, repo: String): Future[List[OptionLibrary]]
  }

  trait NexusRepository {
    def getVersions(artifactName: String, repositoryName: String): Try[List[GAV]]

    def getGavFiles(gav: GAV, repositoryName: String): Try[List[Library]]
  }

}
