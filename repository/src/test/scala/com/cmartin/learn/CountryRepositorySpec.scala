package com.cmartin.learn

import com.cmartin.learn.repository.implementation.CountryRepository
import com.cmartin.learn.repository.tables.{Countries, Country}
import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class CountryRepositorySpec extends RepositorySpec {
  val tableList = List(
    TableQuery[Countries]
  )

  trait Repos {
    val countryRepo = new CountryRepository
  }

  it should "insert a country into the database" in new Repos {
    val count = countryRepo.insert(Country(esCountry._1, esCountry._2))

    count.futureValue shouldBe 1
  }

  it should "insert a sequence of countries into the database" in new Repos {
    val countrySequence = Seq(Country(esCountry._1, esCountry._2), Country(ukCountry._1, ukCountry._2))
    val ids = countryRepo.insert(countrySequence).futureValue

    ids.nonEmpty shouldBe true
    ids.size shouldBe countrySequence.size
    ids.forall(_ > 0L) shouldBe true
  }

  it should "retrieve a country from the database" in new Repos {
    Await.result(countryRepo.insert(Country(esCountry._1, esCountry._2)), waitTimeout)

    val countryOption = countryRepo.findByCode(esCountry._2).futureValue

    countryOption.value.id.value should be > 0L
    countryOption.value.name shouldBe esCountry._1
    countryOption.value.code shouldBe esCountry._2
  }

  it should "update a country from the database" in new Repos {
    val countryId = countryRepo.insert(Country(esCountry._1, esCountry._2)).futureValue
    Await.result(countryRepo.update(Country(esCountry._1.toUpperCase, esCountry._2.toUpperCase, Option(countryId))), waitTimeout)
    val countryOption = countryRepo.findById(countryId).futureValue
    val countryCount = countryRepo.count().futureValue

    countryId should be > 0L
    countryCount shouldBe 1
    countryOption.value.code.forall(_.isUpper) shouldBe true
    countryOption.value.name.forall(_.isUpper) shouldBe true
    countryOption.value.id.value shouldBe countryId
  }

  it should "delete a country from the database" in new Repos {
    val countryId = countryRepo.insert(Country(esCountry._1, esCountry._2)).futureValue
    val initialCount = countryRepo.count().futureValue
    val deleteResult = countryRepo.delete(countryId).futureValue
    val finalCount = countryRepo.count().futureValue

    countryId should be > 0L
    initialCount shouldBe 1
    deleteResult shouldBe 1
    finalCount shouldBe 0
  }

  it should "retrieve all countries from the database" in new Repos {
    Await.result(populateDatabase(), waitTimeout)

    val countries = countryRepo.findAll().futureValue

    countries.nonEmpty shouldBe true
  }


  def populateDatabase() = {
    new Repos {
      val result = for {
        brId <- countryRepo.insert(Country(brCountry._1, brCountry._2))
        esId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
        noId <- countryRepo.insert(Country(noCountry._1, noCountry._2))
        ukId <- countryRepo.insert(Country(ukCountry._1, ukCountry._2))
      } yield ()
    }.result
  }
}
