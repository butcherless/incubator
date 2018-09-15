package com.cmartin.learn.service

import scala.util.Try

package object spec {

  case class Artifact(g: String, a: String, v: String)

  case class Library(g: String, a: String, v: String, d: String, s: String)

  trait DummyService {
    def operationOne(): String

    def searchKey(json: String, key: String): Unit // Option[String]

    def getArtifactVersions(name: String, repo: String): Try[List[Artifact]]
  }

}
