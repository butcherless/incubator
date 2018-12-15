package com.cmartin.learn

import com.cmartin.learn.repository.implementation.{AircraftRepository, AirlineRepository, CountryRepository}
import com.cmartin.learn.repository.tables._
import com.cmartin.learn.test.Constants
import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class AirlineRepositorySpec extends RepositorySpec {
  val tableList = List(
    TableQuery[Countries],
    TableQuery[Airlines],
    TableQuery[Fleet],
    TableQuery[Airports]
  )

  trait Repos {
    val countryRepo = new CountryRepository
    val airlineRepo = new AirlineRepository
    val aircraftRepo = new AircraftRepository
  }

  it should "retrieve an airline from the database" in new Repos {
    val airlineOption = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airlineId <- airlineRepo.insert(Airline(ibsAirline._1, ibsAirline._2, countryId))
      airline <- airlineRepo.findById(airlineId)
    } yield airline

    val airline: Option[Airline] = airlineOption.futureValue

    airline.value.id.value should be > 0L
    airline.value.name shouldBe ibsAirline._1
    airline.value.foundationDate shouldBe ibsAirline._2
  }

  it should "retrieve airline list from a country" in new Repos {
    Await.result(populateDatabase, Constants.waitTimeout)
    val expectedAirlineCount = 2

    val airlines = airlineRepo.findByCountryCode(esCountry._2).futureValue

    airlines.size shouldBe expectedAirlineCount
  }

  def populateDatabase() = {
    new Repos {
      val result = for {
        brId <- countryRepo.insert(Country(brCountry._1, brCountry._2))
        esId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
        noId <- countryRepo.insert(Country(noCountry._1, noCountry._2))
        ukId <- countryRepo.insert(Country(ukCountry._1, ukCountry._2))

        aeaId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, esId))
        ibsId <- airlineRepo.insert(Airline(ibsAirline._1, ibsAirline._2, esId))
        ibkId <- airlineRepo.insert(Airline(ibkAirline._1, ibkAirline._2, noId))
      } yield ()
    }.result
  }
}
