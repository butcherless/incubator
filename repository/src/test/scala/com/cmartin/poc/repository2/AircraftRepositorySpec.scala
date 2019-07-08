package com.cmartin.poc.repository2

import com.cmartin.learn.test.Constants.{aeaAirline, registrationMIG}

import scala.concurrent.Await

class AircraftRepositorySpec extends BaseRepositorySpec {

  behavior of "Aircraft Repository"

  it should "insert an aircraft into the database" in {
    val result = for {
      countryId <- repos.countryRepo.insert(spain)
      airlineId <- repos.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      aircraft <- repos.aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId))
    } yield aircraft

    result map { id => assert(id == 1) }
  }

  override def beforeEach(): Unit = {
    Await.result(repos.countryRepo.create, timeout)
    Await.result(repos.airlineRepo.create, timeout)
    Await.result(repos.aircraftRepo.create, timeout)
  }

  override def afterEach(): Unit = {
    Await.result(repos.aircraftRepo.create, timeout)
    Await.result(repos.airlineRepo.drop, timeout)
    Await.result(repos.countryRepo.drop, timeout)
  }
}
