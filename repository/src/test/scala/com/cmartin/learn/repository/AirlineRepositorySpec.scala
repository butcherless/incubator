package com.cmartin.learn.repository

import java.time.LocalDate

import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues

import scala.concurrent.{Await, Future}

class AirlineRepositorySpec extends BaseRepositorySpec with OptionValues {
  val norway: Country = Country(noCountry._1, noCountry._2)
  //val iberia = Airline(ibkAirline._1, ibkAirline._2, )

  val dal = new DatabaseLayer(config) {
    import profile.api._

    val countryRepo = new CountryRepository
    val airlineRepo = new AirlineRepository

    def createSchema(): Future[Unit] = {
      config.db.run((countries.schema ++ airlines.schema).create)
    }

    def dropSchema(): Future[Unit] = {
      config.db.run((countries.schema ++ airlines.schema).drop)
    }
  }

  import dal.executeFromDb

  behavior of "Airline Repository"

  it should "insert an airline into the database" in {
    val result = insertCountryAirline()

    result map { tuple =>
      assert(tuple._1 > 0)
    }
  }

  it should "update an airline from the database" in {
    val updatedString = "UPDATED"
    val now           = LocalDate.now()
    val result = for {
      (cid, aid) <- insertCountryAirline()
      _          <- dal.airlineRepo.update(Airline(updatedString, now, cid, Option(aid)))
      updated    <- dal.airlineRepo.findById(aid)
    } yield updated.value

    result map { airline =>
      assert(airline.name == updatedString)
      assert(airline.foundationDate == now)
    }
  }

  it should "delete an airline from the database" in {
    val result = for {
      (_, aid) <- insertCountryAirline()
      deleted  <- dal.airlineRepo.delete(aid)
      count    <- dal.airlineRepo.count()
    } yield (aid, deleted, count)

    result map { tuple =>
      assert(tuple._1 == tuple._2)
      assert(tuple._3 == 0)
    }
  }

  it should "retrieve an airline from the database" in {
    val result = for {
      (_, aid) <- insertCountryAirline()
      airline  <- dal.airlineRepo.findById(aid)
    } yield airline.value

    result map { airline =>
      assert(airline.name == ibkAirline._1)
      assert(airline.foundationDate == ibkAirline._2)
      assert(airline.id.contains(1L))
      assert(airline.countryId == 1L)
    }
  }

  it should "retrieve an airline list from a country code" in {
    val expectedCount = 2
    val result = for {
      cid   <- dal.countryRepo.insert(spainCountry)
      _     <- dal.airlineRepo.insert(Airline(ibsAirline._1, ibsAirline._2, cid))
      _     <- dal.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, cid))
      count <- dal.airlineRepo.findByCountryCode(esCountry._2)
    } yield count

    result map { seq =>
      assert(seq.size == expectedCount)
    }
  }

  def insertCountryAirline() =
    for {
      cid <- dal.countryRepo.insert(norway)
      aid <- dal.airlineRepo.insert(Airline(ibkAirline._1, ibkAirline._2, cid))
    } yield (cid, aid)

  override def beforeEach(): Unit = {
    Await.result(dal.createSchema(), waitTimeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.dropSchema(), waitTimeout)
  }
}
