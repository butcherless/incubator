package com.cmartin.learn.repository

import com.cmartin.learn.repository.implementation.{Aircraft, MemoryRepository}
import org.scalatest.OptionValues._
import org.scalatest.{FlatSpec, Matchers}

import scala.language.reflectiveCalls

class RepositorySpec extends FlatSpec with Matchers {

  val typeCodeBoeing  = "B788"
  val typeCodeAirbus  = "A359"
  val registrationMXV = "ec-mxv"
  val registrationMIG = "ec-mig"

  val uuidRegex = "[a-z0-9-]{36}"

  // CREATE
  it should "save an aircraft into the repository" in new Repository {
    val beforeCount = repository.count()
    val a           = newAircraft(typeCodeBoeing, registrationMIG)

    // functionality
    val aSaved = repository.save(a)

    beforeCount.value shouldBe 0
    repository.count().value shouldBe 1
    aSaved.value.nonEmpty shouldBe true
    aSaved.value.matches(uuidRegex) shouldBe true
  }

  // READ
  it should "retrieve an aircraft from the repository" in new Repository {
    // preconditions
    val a      = newAircraft(typeCodeBoeing, registrationMIG)
    val aSaved = repository.save(a)

    // functionality
    val result: Option[Aircraft] = repository.findById(aSaved.value)

    // verifications
    result.value.typeCode shouldEqual a.typeCode
    result.value.registration shouldEqual a.registration
  }

  it should "return None type for not found id" in new Repository {
    // preconditions

    // functionality
    val result: Option[Aircraft] = repository.findById("dummy-id")

    // verifications
    result shouldBe None
  }

  it should "retrieve only airbus aircraft from the repository" in new Repository {
    // preconditions
    val aSaved = repository.save(newAircraft(typeCodeBoeing, registrationMIG))
    val bSaved = repository.save(newAircraft(typeCodeAirbus, registrationMXV))

    // functionality
    val list = repository.findAll(_.typeCode == typeCodeAirbus)

    // verifications
    repository.count().value shouldEqual 2
    list.size shouldEqual 1
    list.value.head.typeCode shouldEqual typeCodeAirbus
  }

  // UPDATE
  it should "update an aircraft into the repository" in new Repository {
    // preconditions
    val a                            = newAircraft(typeCodeBoeing, registrationMIG)
    val aSaved                       = repository.save(a)
    val aRetrieved: Option[Aircraft] = repository.findById(aSaved.value)

    // functionality
    repository.save(
      aRetrieved.value.copy(typeCode = typeCodeAirbus, registration = registrationMXV)
    )
    val aUpdated = repository.findById(aSaved.value)

    // verifications
    aUpdated.value.typeCode shouldEqual typeCodeAirbus
    aUpdated.value.registration shouldEqual registrationMXV
  }

  // REMOVE
  it should "remove an aircraft from the repository" in new Repository {
    // preconditions
    val created     = newAircraft(typeCodeBoeing, registrationMIG)
    val aSaved      = repository.save(created)
    val beforeCount = repository.count()

    // functionality
    val retrieved           = repository.findById(aSaved.value)
    val res: Option[String] = repository.remove(retrieved.value)

    // verifications
    val afterCount = repository.count()
    res.value shouldEqual aSaved.value
    beforeCount.value - afterCount.value shouldEqual 1
  }

  it should "remove only airbus aircraft from the repository" in new Repository {
    // preconditions
    repository.save(newAircraft(typeCodeBoeing, registrationMIG))
    val aSaved = repository.save(newAircraft(typeCodeAirbus, registrationMXV))

    // functionality
    val list = repository.removeAll(_.typeCode == typeCodeAirbus)

    // verifications
    repository.count().value shouldEqual 1
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

  /*
  Test fixture
   */
  trait Repository {
    val repository = MemoryRepository()
  }

  private def newAircraft(typeCode: String, registration: String) =
    Aircraft(typeCode = typeCode, registration = registration)
}
