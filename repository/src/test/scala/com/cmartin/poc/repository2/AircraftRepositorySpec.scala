package com.cmartin.poc.repository2

import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues

import scala.concurrent.Await

class AircraftRepositorySpec extends BaseRepositorySpec with OptionValues {

  behavior of "Aircraft Repository"

  it should "insert an aircraft into the database" in {
    val result = for {
      countryId <- repos.countryRepo.insert(spain)
      airlineId <- repos.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      aircraft <- repos.aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId))
    } yield aircraft

    result map { id => assert(id == 1) }
  }

  it should "retrieve an aircraft from the database by its registration" in {
    val result = for {
      countryId <- repos.countryRepo.insert(spain)
      airlineId <- repos.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      _ <- repos.aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId))
      aircraft <- repos.aircraftRepo.findByRegistration(registrationMIG)
    } yield aircraft.value

    result map { ac =>
      assert(ac.registration == registrationMIG)
      assert(ac.typeCode == TypeCodes.BOEING_787_800)
    }
  }


  it should "retrieve aircraft list from an airline" in {
    val result = for {
      countryId <- repos.countryRepo.insert(spain)
      airlineId <- repos.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      _ <- repos.aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId))
      _ <- repos.aircraftRepo.insert(Aircraft(TypeCodes.AIRBUS_330_200, registrationLVL, airlineId))
      seq <- repos.aircraftRepo.findByAirlineName(aeaAirline._1)
    } yield seq

    result map { seq => assert(seq.size == 2) }
  }

  override def beforeEach(): Unit = {
    Await.result(repos.countryRepo.create, timeout)
    Await.result(repos.airlineRepo.create, timeout)
    Await.result(repos.aircraftRepo.create, timeout)
  }

  override def afterEach(): Unit = {
    Await.result(repos.aircraftRepo.drop, timeout)
    Await.result(repos.airlineRepo.drop, timeout)
    Await.result(repos.countryRepo.drop, timeout)
  }
}
