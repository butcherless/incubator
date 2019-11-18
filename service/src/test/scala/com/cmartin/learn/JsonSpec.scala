package com.cmartin.learn

import com.cmartin.learn.service.impl.{JsonNexusRepository, NexusSettings, getNexusSettings}
import com.cmartin.learn.service.spec
import com.cmartin.learn.service.spec.Library
import org.scalatest.TryValues._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.reflectiveCalls
import scala.util.Try

class JsonSpec extends AnyFlatSpec with Matchers {
  def fixture = new {
    lazy val settings: NexusSettings = getNexusSettings()
    lazy val nexusRepo               = JsonNexusRepository(settings)
  }

  ignore should "return a non empty coordinates (gav) list from nexus repo" in {
    val coordinates =
      fixture.nexusRepo.getVersions(TestConstants.ArtifactName, TestConstants.RepositoryName)

    coordinates.success.value.nonEmpty shouldBe true
    coordinates.success.value.forall(_.version.nonEmpty)
  }

  ignore should "return an empty coordinates (gav) list from nexus repo" in {
    val coordinates = fixture.nexusRepo
      .getVersions(TestConstants.NotExistingArtifactName, TestConstants.RepositoryName)

    coordinates.success.value.isEmpty shouldBe true
  }

  ignore should "return a non empty artifact files list from nexus repo" in {
    val jarList = fixture.nexusRepo.getGavFiles(TestConstants.Gav, TestConstants.RepositoryName)

    jarList.success.value.nonEmpty shouldBe true
    //coordinates.success.value.forall(_.version.nonEmpty)
  }

  ignore should "return a future list of artifacts" in {
    val coordinates: Try[List[spec.GAV]] =
      fixture.nexusRepo.getVersions(TestConstants.ArtifactName, TestConstants.RepositoryName)

    coordinates.success.value.nonEmpty shouldBe true

    val gavList: List[spec.GAV] = coordinates.get

    val futures: List[Future[List[Library]]] =
      gavList.map(fixture.nexusRepo.getAsyncGavFiles(_, TestConstants.RepositoryName))

    val traverseResult: Future[List[List[Library]]] = Future.traverse(futures) { f =>
      f
    }

    traverseResult.onComplete {
      case scala.util.Success(list) => {
        val result = list.flatten
        println(s"list size: ${result.size}")
        result.foreach(println(_))
      }
      case scala.util.Failure(exception) => println(List.empty[Library])
    }
  }
}
