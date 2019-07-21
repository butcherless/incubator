package com.cmartin.poc.repository2

import java.sql.SQLIntegrityConstraintViolationException

import com.cmartin.learn.repository.tables.TypeCodes
import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues

import scala.concurrent.{Await, Future}

class AircraftRepositorySpec extends BaseRepositorySpec with OptionValues {

  val dal = new DatabaseAccessLayer2(config) {

    import profile.api._

    val countryRepo = new CountryRepository(config.db)
    val airlineRepo = new AirlineRepository(config.db)
    val aircraftRepo = new AircraftRepository(config.db)

    def createSchema(): Future[Unit] = {
      config.db.run(
        (countries.schema ++ airlines.schema ++ fleet.schema)
          .create)
    }

    def dropSchema(): Future[Unit] = {
      config.db.run(
        (countries.schema ++ airlines.schema ++ fleet.schema)
          .drop )
    }
  }

  "Aircraft Repository" should "insert an aircraft into the database" in {
    val result = for {
      countryId <- dal.countryRepo.insert(spainCountry)
      airlineId <- dal.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      aircraft <- dal.aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId))
    } yield aircraft

    result map { id => assert(id > 0) }
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
      countryId <- dal.countryRepo.insert(spainCountry)
      airlineId <- dal.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      _ <- dal.aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId))
      aircraft <- dal.aircraftRepo.findByRegistration(registrationMIG)
    } yield aircraft.value

    result map { ac =>
      assert(ac.registration == registrationMIG)
      assert(ac.typeCode == TypeCodes.BOEING_787_800)
    }
  }

  it should "retrieve aircraft list from an airline" in {
    val result = for {
      countryId <- dal.countryRepo.insert(spainCountry)
      airlineId <- dal.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      _ <- dal.aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId))
      _ <- dal.aircraftRepo.insert(Aircraft(TypeCodes.AIRBUS_330_200, registrationLVL, airlineId))
      seq <- dal.aircraftRepo.findByAirlineName(aeaAirline._1)
    } yield seq

    result map { seq => assert(seq.size == 2) }
  }

  override def beforeEach(): Unit = {
    Await.result(dal.createSchema(), timeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.dropSchema(), timeout)
  }

}
