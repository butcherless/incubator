package com.cmartin.learn

import com.cmartin.learn.repository.impl.{Aircraft, MemoryRepository}
import org.scalatest.OptionValues._
import org.scalatest.{FlatSpec, Matchers}

import scala.language.reflectiveCalls

class RepositorySpec extends FlatSpec with Matchers {


  def fixture = new {
    lazy val repository = MemoryRepository()
  }


  it should "save an aircraft into the repository" in {
    val repo = fixture.repository
    val beforeCount = repo.count()
    val a = newAircraft("B787", "ec-mab")

    // functionality
    val aSaved = repo.save(a)

    beforeCount.value shouldBe 0
    repo.count().value shouldBe 1
    aSaved.value.nonEmpty shouldBe true
  }


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

  it should "not retrieve an aircraft from the repository" in {
    // preconditions
    val repo = fixture.repository

    // functionality
    val result: Option[Aircraft] = repo.findById("dummy-id")

    // verifications
    result shouldBe None
  }

  def newAircraft(typeCode: String, registration: String) = Aircraft(typeCode = typeCode, registration = registration)
}
