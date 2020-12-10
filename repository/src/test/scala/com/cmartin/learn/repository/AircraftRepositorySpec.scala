package com.cmartin.learn.repository

import java.sql.SQLIntegrityConstraintViolationException

import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues

import scala.concurrent.{Await, Future}

class AircraftRepositorySpec extends BaseRepositorySpec with OptionValues {

  val dal = new DatabaseLayer(config) {
    import profile.api._

    val countryRepo  = new CountryRepository
    val airlineRepo  = new AirlineRepository
    val aircraftRepo = new AircraftRepository

    def createSchema(): Future[Unit] = {
      config.db.run((countries.schema ++ airlines.schema ++ fleet.schema).create)
    }

    def dropSchema(): Future[Unit] = {
      config.db.run((countries.schema ++ airlines.schema ++ fleet.schema).drop)
    }
  }

  import dal.executeFromDb

  behavior of "Aircraft Repository"

  it should "insert an aircraft into the database" in {
    val result = for {
      airlineId <- insertCountryAirline()
      aircraft <- dal.aircraftRepo.insert(
        Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId)
      )
    } yield aircraft

    result map { id =>
      assert(id > 0)
    }
  }

  it should "fail to insert an aircraft into the database with a missing airline" in {
    recoverToSucceededIf[SQLIntegrityConstraintViolationException] {
      for {
        _ <- dal.aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, 0))
      } yield ()
    }
  }

  it should "retrieve an aircraft from the database by its registration" in {
    val result = for {
      airlineId <- insertCountryAirline()
      _         <- dal.aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId))
      aircraft  <- dal.aircraftRepo.findByRegistration(registrationMIG)
    } yield aircraft.value

    result map { ac =>
      assert(ac.registration == registrationMIG)
      assert(ac.typeCode == TypeCodes.BOEING_787_800)
    }
  }

  it should "retrieve aircraft list from an airline" in {
    val result = for {
      airlineId <- insertCountryAirline()
      _         <- dal.aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId))
      _         <- dal.aircraftRepo.insert(Aircraft(TypeCodes.AIRBUS_330_200, registrationLVL, airlineId))
      seq       <- dal.aircraftRepo.findByAirlineName(aeaAirline._1)
    } yield seq

    result map { seq =>
      assert(seq.size == 2)
    }
  }

  def insertCountryAirline() =
    for {
      countryId <- dal.countryRepo.insert(spainCountry)
      airlineId <- dal.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
    } yield airlineId

  override def beforeEach(): Unit = {
    Await.result(dal.createSchema(), waitTimeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.dropSchema(), waitTimeout)
  }
}
