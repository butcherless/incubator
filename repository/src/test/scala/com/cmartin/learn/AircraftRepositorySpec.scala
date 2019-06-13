package com.cmartin.learn

import com.cmartin.learn.repository.implementation.{AircraftRepository, AirlineRepository, CountryRepository}
import com.cmartin.learn.repository.tables._
import com.cmartin.learn.test.Constants
import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

class AircraftRepositorySpec extends RepositorySpec {
  val tableList = List(
    TableQuery[Countries],
    TableQuery[Airlines],
    TableQuery[Fleet],
    TableQuery[Airports]
  )

  trait Repos {
    val countryRepo  = new CountryRepository
    val airlineRepo  = new AirlineRepository
    val aircraftRepo = new AircraftRepository
  }

  it should "insert an aircraft into the database" in new Repos {

    val aircraftFuture = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airlineId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      aircraftId <- aircraftRepo.insert(
        Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId)
      )
    } yield aircraftId

    val aircraftId = aircraftFuture.futureValue
    aircraftId should be > 0L
  }

  it should "retrieve an aircraft from the database" in new Repos {

    val aircraftFuture: Future[Option[Aircraft]] = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airlineId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      aircraftId <- aircraftRepo.insert(
        Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId)
      )
      aircraft <- aircraftRepo.findById(aircraftId)
    } yield aircraft

    val aircraft = aircraftFuture.futureValue.value
    aircraft.id.value should be > 0L
    aircraft.typeCode shouldEqual TypeCodes.BOEING_787_800
    aircraft.registration shouldEqual registrationMIG
    aircraft.airlineId should be > 0L
  }

  it should "update an aircraft into the database" in new Repos {
    val aircraftResult = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airlineId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      _         <- aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId))
      aircraft  <- aircraftRepo.findByRegistration(registrationMIG)
      _         <- aircraftRepo.update(aircraft.value.copy(registration = registrationMNS))
      aircraft  <- aircraftRepo.findByRegistration(registrationMNS)
    } yield aircraft

    val aircraft = aircraftResult.futureValue
    aircraft.value.registration shouldBe registrationMNS
  }

  it should "delete an aircraft from the dataase" in new Repos {
    val initialCount = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airlineId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      _         <- aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId))
      count     <- aircraftRepo.count
    } yield count

    initialCount.futureValue shouldBe 1

    val finalCount = for {
      aircraft <- aircraftRepo.findByRegistration(registrationMIG)
      _        <- aircraftRepo.delete(aircraft.value.id.value)
      count    <- aircraftRepo.count
    } yield count

    finalCount.futureValue shouldBe 0
  }

  it should "retrieve aircraft list from an airline" in new Repos {
    Await.result(populateDatabase, Constants.waitTimeout)
    val expectedAircfraftCount = 2

    val aircrafts = aircraftRepo.findByAirlineName(aeaAirline._1).futureValue

    aircrafts.size shouldBe expectedAircfraftCount
  }

  def populateDatabase() = {
    new Repos {
      val result = for {
        brId <- countryRepo.insert(Country(brCountry._1, brCountry._2))
        esId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
        noId <- countryRepo.insert(Country(noCountry._1, noCountry._2))
        ukId <- countryRepo.insert(Country(ukCountry._1, ukCountry._2))

        aeaId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, esId))

        aircraftId <- aircraftRepo.insert(Aircraft(ecMigAircraft._1, ecMigAircraft._2, aeaId))
        _          <- aircraftRepo.insert(Aircraft(ecLvlAircraft._1, ecLvlAircraft._2, aeaId))

      } yield ()
    }.result
  }

}
