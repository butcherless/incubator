package com.cmartin.learn

import com.cmartin.learn.repository.impl.{Aircraft, MemoryRepository}
import org.scalatest.OptionValues._
import org.scalatest.{FlatSpec, Matchers}

import scala.language.reflectiveCalls

class RepositorySpec extends FlatSpec with Matchers {

  val typeCodeBoeing = "B788"
  val typeCodeAirbus = "A359"
  val registrationMXV = "ec-mxv"
  val registrationMIG = "ec-mig"

  val uuidRegex = "[a-z0-9-]{36}"

  // CREATE
  it should "save an aircraft into the repository" in {
    val repo = fixture.repository
    val beforeCount = repo.count()
    val a = newAircraft(typeCodeBoeing, registrationMIG)

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
    val a = newAircraft(typeCodeBoeing, registrationMIG)
    val aSaved = repo.save(a)

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

  it should "retrieve only airbus aircraft from the repository" in {
    // preconditions
    val repo = fixture.repository
    val aSaved = repo.save(newAircraft(typeCodeBoeing, registrationMIG))
    val bSaved = repo.save(newAircraft(typeCodeAirbus, registrationMXV))

    // functionality
    val list = repo.findAll(_.typeCode == typeCodeAirbus)

    // verifications
    repo.count().value shouldEqual 2
    list.size shouldEqual 1
    list.value.head.typeCode shouldEqual typeCodeAirbus
  }


  // UPDATE
  it should "update an aircraft into the repository" in {
    // preconditions
    val repo = fixture.repository
    val a = newAircraft(typeCodeBoeing, registrationMIG)
    val aSaved = repo.save(a)
    val aRetrieved: Option[Aircraft] = repo.findById(aSaved.value)

    // functionality
    repo.save(aRetrieved.value.copy(typeCode = typeCodeAirbus, registration = registrationMXV))
    val aUpdated = repo.findById(aSaved.value)

    // verifications
    aUpdated.value.typeCode shouldEqual typeCodeAirbus
    aUpdated.value.registration shouldEqual registrationMXV
  }


  // REMOVE
  it should "remove an aircraft from the repository" in {
    // preconditions
    val repo = fixture.repository
    val created = newAircraft(typeCodeBoeing, registrationMIG)
    val aSaved = repo.save(created)
    val beforeCount = repo.count()

    // functionality
    val retrieved = repo.findById(aSaved.value)
    val res: Option[String] = repo.remove(retrieved.value)

    // verifications
    val afterCount = repo.count()
    res.value shouldEqual aSaved.value
    beforeCount.value - afterCount.value shouldEqual 1
  }

  it should "remove only airbus aircraft from the repository" in {
    // preconditions
    val repo = fixture.repository
    repo.save(newAircraft(typeCodeBoeing, registrationMIG))
    val aSaved = repo.save(newAircraft(typeCodeAirbus, registrationMXV))

    // functionality
    val list = repo.removeAll(_.typeCode == typeCodeAirbus)

    // verifications
    repo.count().value shouldEqual 1
    list.size shouldEqual 1
    list.value.head shouldEqual aSaved.value
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
