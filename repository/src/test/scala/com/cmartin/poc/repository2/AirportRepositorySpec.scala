package com.cmartin.poc.repository2

import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues

import scala.concurrent.Await


class AirportRepositorySpec extends BaseRepositorySpec with OptionValues {

  val dal = new DatabaseAccessLayer2(config) {
    val countryRepo = new CountryRepository(config.db)
    val airportRepo = new AirportRepository(config.db)
  }

  behavior of "Airport Repository"

  it should "create an airport into the database" in {

    val result = for {
      countryId <- dal.countryRepo.insert(spain)
      id <- dal.airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, countryId))
    } yield id

    result map { id => assert(id == 1) }
  }

  it should "retrieve an airport list from a country code" in {

    val result = for {
      countryId <- dal.countryRepo.insert(spain)
      _ <- dal.airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, countryId))
      _ <- dal.airportRepo.insert(Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, countryId))
      seq <- dal.airportRepo.findByCountryCode(spain.code)
    } yield seq

    result map { seq => assert(seq.size == 2) }
  }


  override def beforeEach(): Unit = {
    Await.result(dal.countryRepo.create, timeout)
    Await.result(dal.airportRepo.create, timeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.airportRepo.drop, timeout)
    Await.result(dal.countryRepo.drop, timeout)
  }
}
