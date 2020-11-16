package com.cmartin.learn.repository

import java.sql.SQLIntegrityConstraintViolationException

import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues

import scala.concurrent.{Await, Future}

class AirportRepositorySpec extends BaseRepositorySpec with OptionValues {
  val dal = new DatabaseLayer(config) {
    import profile.api._

    val countryRepo = new CountryRepository
    val airportRepo = new AirportRepository

    def createSchema(): Future[Unit] = {
      config.db.run((countries.schema ++ airports.schema).create)
    }

    def dropSchema(): Future[Unit] = {
      config.db.run((airports.schema ++ countries.schema).drop)
    }
  }

  import dal.executeFromDb

  behavior of "Airport Repository"

  it should "create an airport into the database" in {
    val result = for {
      countryId <- dal.countryRepo.insert(spainCountry)
      id        <- dal.airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, countryId))
    } yield id

    result map { id =>
      assert(id > 0)
    }
  }

  it should "fail to insert an airport into the database with a missing country" in {
    recoverToSucceededIf[SQLIntegrityConstraintViolationException] {
      for {
        _ <- dal.airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, 0))
      } yield ()
    }
  }

  it should "retrieve an airport list from a country code" in {
    val result = for {
      countryId <- dal.countryRepo.insert(spainCountry)
      _         <- dal.airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, countryId))
      _         <- dal.airportRepo.insert(Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, countryId))
      seq       <- dal.airportRepo.findByCountryCode(spainCountry.code)
    } yield seq

    result map { seq =>
      assert(seq.size == 2)
    }
  }

  override def beforeEach(): Unit = {
    Await.result(dal.createSchema(), timeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.dropSchema(), timeout)
  }
}
