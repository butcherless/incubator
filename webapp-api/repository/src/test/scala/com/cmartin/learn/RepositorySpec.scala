package com.cmartin.learn

import com.cmartin.learn.repository.impl.{Aircraft, MemoryRepository}
import org.scalatest.OptionValues._
import org.scalatest.{FlatSpec, Matchers}

import scala.language.reflectiveCalls

class RepositorySpec extends FlatSpec with Matchers {

  val uuidRegex = "[a-z0-9-]{36}"

  // CREATE
  it should "save an aircraft into the repository" in {
    val repo = fixture.repository
    val beforeCount = repo.count()
    val a = newAircraft("B787", "ec-mab")

    // functionality
    val aSaved = repo.save(a)

    beforeCount.value shouldBe 0
    repo.count().value shouldBe 1
    aSaved.value.nonEmpty shouldBe true
    aSaved.value.matches(uuidRegex) shouldBe true
  }


  // READ
  it should "retrieve an aircraft from the repository" in {
    // preconditions
    val repo = fixture.repository
    val a = newAircraft("B787", "ec-mab")
    val aSaved = repo.save(a)
    aSaved.value.nonEmpty shouldBe true

    // functionality
    val result: Option[Aircraft] = repo.findById(aSaved.value)

    // verifications
    result.value.typeCode shouldEqual a.typeCode
    result.value.registration shouldEqual a.registration
  }

  it should "return None type for not found id" in {
    // preconditions
    val repo = fixture.repository

    // functionality
    val result: Option[Aircraft] = repo.findById("dummy-id")

    // verifications
    result shouldBe None
  }


  // UPDATE
  it should "update an aircraft into the repository" in {
    val typeCode = "A359"
    val registration = "ec-mig"
    // preconditions
    val repo = fixture.repository
    val a = newAircraft("B787", "ec-mab")
    val aSaved = repo.save(a)
    aSaved.value.nonEmpty shouldBe true
    val aRetrieved: Option[Aircraft] = repo.findById(aSaved.value)
    aRetrieved.isDefined shouldBe true

    // functionality
    val aUpdated: Aircraft = aRetrieved.value.copy(typeCode = typeCode, registration = registration)

    // verifications
    aUpdated.typeCode shouldEqual typeCode
    aUpdated.registration shouldEqual registration
  }


  /*
   _    _   ______   _        _____    ______   _____     _____
  | |  | | |  ____| | |      |  __ \  |  ____| |  __ \   / ____|
  | |__| | | |__    | |      | |__) | | |__    | |__) | | (___
  |  __  | |  __|   | |      |  ___/  |  __|   |  _  /   \___ \
  | |  | | | |____  | |____  | |      | |____  | | \ \   ____) |
  |_|  |_| |______| |______| |_|      |______| |_|  \_\ |_____/
  */


  def fixture = new {
    lazy val repository = MemoryRepository()
  }

  private def newAircraft(typeCode: String, registration: String) = Aircraft(typeCode = typeCode, registration = registration)
}
