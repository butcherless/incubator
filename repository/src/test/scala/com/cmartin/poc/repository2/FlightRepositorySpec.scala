package com.cmartin.poc.repository2

import java.sql.SQLIntegrityConstraintViolationException

import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues

import scala.concurrent.Await

class FlightRepositorySpec extends BaseRepositorySpec with OptionValues {

  val dal = new DatabaseAccessLayer2(config) {

    import profile.api._

    val countryRepo = new CountryRepository(config.db)
    val airportRepo = new AirportRepository(config.db)
    val airlineRepo = new AirlineRepository(config.db)
    val routeRepo = new RouteRepository(config.db)
    val flightRepo = new FlightRepository(config.db)

    def createSchema() = {
      config.db.run(
        (countries.schema ++ airports.schema ++ airlines.schema ++
          routes.schema ++ flights.schema)
          .create)
    }

    def dropSchema() = {
      config.db.run(
        (countries.schema ++ airports.schema ++ airlines.schema ++
          routes.schema ++ flights.schema)
          .drop)
    }
  }

  "Flight Repository" should "insert a flight into the database" in {
    val result = for {
      countryId <- dal.countryRepo.insert(spainCountry)
      madId <- dal.airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, countryId))
      tfnId <- dal.airportRepo.insert(Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, countryId))
      aeaId <- dal.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      madTfnId <- dal.routeRepo.insert(Route(957.0, madId, tfnId))

      flightId <- dal.flightRepo.insert(
        Flight(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4, aeaId, madTfnId))
    } yield flightId

    result map { id =>
      assert(id > 0)
    }
  }

  it should "fail to insert a flight into the database with missing airline" in {
    recoverToSucceededIf[SQLIntegrityConstraintViolationException] {
      for {
        countryId <- dal.countryRepo.insert(spainCountry)
        madId <- dal.airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, countryId))
        tfnId <- dal.airportRepo.insert(Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, countryId))
        madTfnId <- dal.routeRepo.insert(Route(957.0, madId, tfnId))

        _ <- dal.flightRepo.insert(
          Flight(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4, 0, madTfnId))
      } yield ()
    }
  }

  it should "fail to insert a flight into the database with missing route" in {
    recoverToSucceededIf[SQLIntegrityConstraintViolationException] {
      for {
        countryId <- dal.countryRepo.insert(spainCountry)
        aeaId <- dal.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))

        _ <- dal.flightRepo.insert(
          Flight(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4, aeaId, 0))
      } yield ()
    }
  }


  def insertCountryAirportAirlineRoute() = for {
    countryId <- dal.countryRepo.insert(spainCountry)
    aeaId <- dal.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
    madId <- dal.airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, countryId))
    tfnId <- dal.airportRepo.insert(Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, countryId))
    madTfnId <- dal.routeRepo.insert(Route(957.0, madId, tfnId))
  } yield (aeaId, madTfnId)


  override def beforeEach(): Unit = {
    Await.result(dal.createSchema(), timeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.dropSchema(), timeout)
  }
}
