package com.cmartin.learn.application

import com.cmartin.learn.adapter.postgres.SlickRepositories.Database2Layer
import com.cmartin.learn.domain.ApplicationPorts.CountryService
import com.cmartin.learn.domain.Model.Country
import com.cmartin.learn.test.AviationData.Constants.waitTimeout
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Await

class CountryCrudServiceSpec /*                                            */
    extends AsyncFlatSpec
    with Matchers
    with BeforeAndAfterEach {

  import CountryUseCasesSpec._

  behavior of "CountryCrudService"

  val dal = new Database2Layer("h2_dc") {
    import profile.api._

    private val schema = airports.schema ++ countries.schema

    def createSchema() =
      countries.schema.create.zip(airports.schema.create)

    def dropSchema() =
      airports.schema.dropIfExists.zip(countries.schema.dropIfExists)

  }

  val countryService: CountryService = new CountryCrudService(dal)

  it should "create a country" in {
    // given
    val country = Country("spain", countryCode)

    // when
    val result = for {
      _      <- countryService.create(country)
      exists <- countryService.exists(country)
    } yield exists

    // then
    result map { e =>
      e shouldBe true
    }
  }

  it should "update a country" in {
    // given
    val country        = Country("Spain", countryCode)
    val updatedCountry = Country("SPAIN-UPDATED", countryCode)

    // when
    val result = for {
      _         <- countryService.create(country)
      _         <- countryService.update(updatedCountry)
      retrieved <- countryService.searchByCode(countryCode)
    } yield retrieved

    // then
    result map { c =>
      c shouldBe updatedCountry
    }
  }

  it should "delete a country" in {
    // given
    val country = Country("Spain", countryCode)

    // when
    val result = for {
      _      <- countryService.create(country)
      _      <- countryService.delete(country)
      exists <- countryService.exists(country)
    } yield exists

    // then
    result map { e =>
      e shouldBe false
    }
  }

  it should "find a country by code" in {
    // given
    val country = Country("Spain", countryCode)

    // when
    val result = for {
      _         <- countryService.create(country)
      retrieved <- countryService.searchByCode(countryCode)
    } yield retrieved

    // then
    result map { c =>
      c shouldBe country
    }
  }

  override def beforeEach(): Unit = {
    import dal.executeFromDb
    Await.result(dal.dropSchema(), waitTimeout)
    Await.result(dal.createSchema(), waitTimeout)
  }

}

object CountryUseCasesSpec {
  val countryCode = "ES"
}
