package com.cmartin.learn

import com.cmartin.learn.repository.implementation._
import com.cmartin.learn.repository.tables._
import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class FlightRepositorySpec extends RepositorySpec {
  val tableList = List(
    TableQuery[Countries],
    TableQuery[Airlines],
    TableQuery[Airports],
    TableQuery[Routes],
    TableQuery[Flights]
  )

  trait Repos {
    val airlineRepo = new AirlineRepository
    val airportRepo = new AirportRepository
    val countryRepo = new CountryRepository
    val flightRepo  = new FlightRepository
    val routeRepo   = new RouteRepository
  }

  it should "retrieve flight by code" in new Repos {
    Await.result(populateDatabase, waitTimeout)

    val flightOption = flightRepo.findByCode(flightUx9059._1).futureValue

    flightOption.value.code shouldBe flightUx9059._1
    flightOption.value.alias shouldBe flightUx9059._2
  }

  it should "retrieve all flights for a given route" in new Repos {
    Await.result(populateDatabase, waitTimeout)
    val expectedFlightCount = 2
    val expectedSet         = Set(flightUx9059._1, flightI23942._1)

    val flights = flightRepo.findByOrigin(barajasIataCode).futureValue

    flights.size shouldBe expectedFlightCount
    flights.map(_.code).toSet diff expectedSet shouldBe Set.empty
  }

  def populateDatabase() = {
    new Repos {
      val result = for {
        brId <- countryRepo.insert(Country(brCountry._1, brCountry._2))
        esId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
        noId <- countryRepo.insert(Country(noCountry._1, noCountry._2))
        ukId <- countryRepo.insert(Country(ukCountry._1, ukCountry._2))

        bcnId <- airportRepo.insert(Airport(bcnAirport._1, bcnAirport._2, bcnAirport._3, esId))
        madId <- airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, esId))
        tfnId <- airportRepo.insert(Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, esId))

        aeaId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, esId))
        ibkId <- airlineRepo.insert(Airline(ibkAirline._1, ibkAirline._2, noId))
        ibsId <- airlineRepo.insert(Airline(ibsAirline._1, ibsAirline._2, esId))

        bcnTfnId <- routeRepo.insert(Route(1185.0, bcnId, tfnId)) // 3 destinations
        madTfnId <- routeRepo.insert(Route(957.0, madId, tfnId))

        ux9059Id <- flightRepo.insert(
          Flight(
            flightUx9059._1,
            flightUx9059._2,
            flightUx9059._3,
            flightUx9059._4,
            aeaId,
            madTfnId
          )
        )
        _ <- flightRepo.insert(
          Flight(
            flightI23942._1,
            flightI23942._2,
            flightI23942._3,
            flightI23942._4,
            ibsId,
            madTfnId
          )
        )
        d85756Id <- flightRepo.insert(
          Flight(
            flightD85756._1,
            flightD85756._2,
            flightD85756._3,
            flightD85756._4,
            ibkId,
            bcnTfnId
          )
        )

      } yield ()
    }.result
  }
}
