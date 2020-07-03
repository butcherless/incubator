package com.cmartin.learn.repository

import java.sql.SQLIntegrityConstraintViolationException

import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues

import scala.concurrent.Await

class FlightRepositorySpec extends BaseRepositorySpec with OptionValues {
  val dal = new DatabaseAccessLayer2(config) {
    import profile.api._

    val countryRepo = new CountryRepository
    val airportRepo = new AirportRepository
    val airlineRepo = new AirlineRepository
    val routeRepo   = new RouteRepository
    val flightRepo  = new FlightRepository

    def createSchema() = {
      config.db.run(
        (countries.schema ++ airports.schema ++ airlines.schema ++
          routes.schema ++ flights.schema).create
      )
    }

    def dropSchema() = {
      config.db.run(
        (countries.schema ++ airports.schema ++ airlines.schema ++
          routes.schema ++ flights.schema).drop
      )
    }
  }

  "Flight Repository" should "insert a flight into the database" in {
    val result = for {
      (aeaId, madTfnId) <- insertCountryAirportAirlineRoute()

      flightId <- dal.flightRepo.insert(
        Flight(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4, aeaId, madTfnId)
      )
    } yield flightId

    result map { id =>
      assert(id > 0)
    }
  }

  it should "fail to insert a flight into the database with missing airline" in {
    recoverToSucceededIf[SQLIntegrityConstraintViolationException] {
      for {
        countryId <- dal.countryRepo.insert(spainCountry)
        madId <- dal.airportRepo.insert(
          Airport(madAirport._1, madAirport._2, madAirport._3, countryId)
        )
        tfnId <- dal.airportRepo.insert(
          Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, countryId)
        )
        madTfnId <- dal.routeRepo.insert(Route(957.0, madId, tfnId))

        _ <- dal.flightRepo.insert(
          Flight(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4, 0, madTfnId)
        )
      } yield ()
    }
  }

  it should "fail to insert a flight into the database with missing route" in {
    recoverToSucceededIf[SQLIntegrityConstraintViolationException] {
      for {
        countryId <- dal.countryRepo.insert(spainCountry)
        aeaId     <- dal.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))

        _ <- dal.flightRepo.insert(
          Flight(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4, aeaId, 0)
        )
      } yield ()
    }
  }

  it should "delete a flight from the database" in {
    val result = for {
      (aeaId, madTfnId) <- insertCountryAirportAirlineRoute()

      id <- dal.flightRepo.insert(
        Flight(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4, aeaId, madTfnId)
      )
      deleted <- dal.flightRepo.delete(id)
      count   <- dal.flightRepo.count()
    } yield (id, deleted, count)

    result map { tuple =>
      assert(tuple._1 == tuple._2)
      assert(tuple._3 == 0)
    }
  }

  it should "find a flight by code" in {
    val result = for {
      (aeaId, madTfnId) <- insertCountryAirportAirlineRoute()
      _ <- dal.flightRepo.insert(
        Flight(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4, aeaId, madTfnId)
      )

      flight <- dal.flightRepo.findByCode(flightUx9059._1)
    } yield flight.value

    result map { flight =>
      assert(flight.code == flightUx9059._1)
    }
  }

  it should "fail to find a flight by non-existent code" in {
    val result = for {
      (aeaId, madTfnId) <- insertCountryAirportAirlineRoute()
      _ <- dal.flightRepo.insert(
        Flight(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4, aeaId, madTfnId)
      )

      flight <- dal.flightRepo.findByCode("unknown")
    } yield flight

    result map { flight =>
      assert(flight.isEmpty)
    }
  }

  it should "find a flight by origin airport" in {
    val result = for {
      countryId <- dal.countryRepo.insert(spainCountry)
      aeaId     <- dal.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      ibsId     <- dal.airlineRepo.insert(Airline(ibsAirline._1, ibsAirline._2, countryId))
      madId <- dal.airportRepo.insert(
        Airport(madAirport._1, madAirport._2, madAirport._3, countryId)
      )
      tfnId <- dal.airportRepo.insert(
        Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, countryId)
      )
      madTfnId <- dal.routeRepo.insert(Route(957.0, madId, tfnId))

      _ <- dal.flightRepo.insert(
        Seq(
          Flight(
            flightUx9059._1,
            flightUx9059._2,
            flightUx9059._3,
            flightUx9059._4,
            aeaId,
            madTfnId
          ),
          Flight(
            flightI23942._1,
            flightI23942._2,
            flightI23942._3,
            flightI23942._4,
            ibsId,
            madTfnId
          )
        )
      )

      flights <- dal.flightRepo.findByOrigin(barajasIataCode)
    } yield flights

    result map { fs =>
      assert(fs.size == 2)
    }
  }

  def insertCountryAirportAirlineRoute() =
    for {
      countryId <- dal.countryRepo.insert(spainCountry)
      aeaId     <- dal.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      madId <- dal.airportRepo.insert(
        Airport(madAirport._1, madAirport._2, madAirport._3, countryId)
      )
      tfnId <- dal.airportRepo.insert(
        Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, countryId)
      )
      madTfnId <- dal.routeRepo.insert(Route(957.0, madId, tfnId))
    } yield (aeaId, madTfnId)

  override def beforeEach(): Unit = {
    Await.result(dal.createSchema(), timeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.dropSchema(), timeout)
  }
}
