package com.cmartin.learn.service

import scala.util.Try

package object spec {
  type OptionLibrary = Option[Library]

  case class Artifact(g: String, a: String, v: String)

  case class Library(g: String, a: String, v: String, d: String, s: String)

  case class GAV(groupId: String, artifactId: String, version: String)

  trait NexusRepository {
    def getVersions(artifactName: String, repositoryName: String): Try[List[GAV]]

    def getGavFiles(gav: GAV, repositoryName: String): Try[List[Library]]
  }

  // REMOVE

  trait DummyService {
    def operationOne(): String
  }

  /**
    * P.O.C.
    */
  trait AbstractNexusRepository[M[_]] {
    def operationOne(s: String): M[Int]

    def operationTwo(n: Int): M[Boolean]
  }
}
