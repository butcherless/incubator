package com.cmartin.learn

import com.cmartin.learn.repository.implementation.{AirportRepository, CountryRepository}
import com.cmartin.learn.repository.tables.{Airport, Airports, Countries, Country}
import com.cmartin.learn.test.Constants
import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class AirportRepositorySpec extends RepositorySpec {
  val tableList = List(
    TableQuery[Countries],
    TableQuery[Airports]
  )

  trait Repos {
    val countryRepo = new CountryRepository
    val airportRepo = new AirportRepository
  }


  it should "insert an airport and a country" in new Repos {

    val airportOption = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airportId <- airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, countryId))
      airport <- airportRepo.findById(airportId)
    } yield airport


    // asserts
    val airport = airportOption.futureValue.value

    airport.id.value should be > 0L
    airport.countryId should be > 0L
    airport.name shouldBe madAirport._1
    airport.iataCode shouldBe madAirport._2
    airport.icaoCode shouldBe madAirport._3
  }


  it should "retrieve an Airport empty collection" in new Repos {
    val countries = airportRepo.findByCountryCode(esCountry._2).futureValue

    countries.isEmpty shouldBe true
  }

  it should "retrieve airport list from a country" in new Repos {
    Await.result(populateDatabase, Constants.waitTimeout)
    val expectedAirportCount = 3

    val airports = airportRepo.findByCountryCode(esCountry._2).futureValue
    airports.size shouldBe expectedAirportCount
  }

  def populateDatabase() = {
    new Repos {
      val result = for {
        brId <- countryRepo.insert(Country(brCountry._1, brCountry._2))
        esId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
        noId <- countryRepo.insert(Country(noCountry._1, noCountry._2))
        ukId <- countryRepo.insert(Country(ukCountry._1, ukCountry._2))

        madId <- airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, esId))
        tfnId <- airportRepo.insert(Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, esId))
        bcnId <- airportRepo.insert(Airport(bcnAirport._1, bcnAirport._2, bcnAirport._3, esId))
        lhrId <- airportRepo.insert(Airport(lhrAirport._1, lhrAirport._2, lhrAirport._3, ukId))
        lgwId <- airportRepo.insert(Airport(lgwAirport._1, lgwAirport._2, lgwAirport._3, ukId))
        _ <- airportRepo.insert(Airport(bsbAirport._1, bsbAirport._2, bsbAirport._3, brId))
        _ <- airportRepo.insert(Airport(gigAirport._1, gigAirport._2, gigAirport._3, brId))
        _ <- airportRepo.insert(Airport(ssaAirport._1, ssaAirport._2, ssaAirport._3, brId))

      } yield ()
    }.result
  }
}
