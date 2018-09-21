package com.cmartin.learn

import com.cmartin.learn.service.impl.{JsonNexusRepository, NexusSettings, getNexusSettings}
import org.scalatest.TryValues._
import org.scalatest.{FlatSpec, Matchers}

class JsonSpec extends FlatSpec with Matchers {


  def fixture = new {
    lazy val settings: NexusSettings = getNexusSettings()
    lazy val nexusRepo = JsonNexusRepository(settings)
  }

  //"Json repository get coordinates list"
  ignore should "return a non empty coordinates (gav) list from nexus repo" in {
    val coordinates = fixture.nexusRepo.getVersions(TestConstants.ArtifactName, TestConstants.RepositoryName)

    coordinates.success.value.nonEmpty shouldBe (true)
    coordinates.success.value.forall(_.version.nonEmpty)
  }

  //"Json repository get coordinates list"
  ignore should "return an empty coordinates (gav) list from nexus repo" in {
    val coordinates = fixture.nexusRepo.getVersions(TestConstants.NotExistingArtifactName, TestConstants.RepositoryName)

    coordinates.success.value.isEmpty shouldBe (true)
  }

  //"Json repository get files list"
  ignore should "return a non empty artifact files list from nexus repo" in {
    val jarList = fixture.nexusRepo.getGavFiles(TestConstants.Gav, TestConstants.RepositoryName)

    jarList.success.value.nonEmpty shouldBe (true)
    //coordinates.success.value.forall(_.version.nonEmpty)
  }

}
