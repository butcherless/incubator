package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.adapter.postgres.Model.AirportDbo
import com.cmartin.learn.adapter.postgres.SlickRepositories.DatabaseLayer
import com.cmartin.learn.test.AviationData.Constants.{esCountry, waitTimeout}

import scala.concurrent.{Await, Future}

abstract class AirportRepositorySpec(path: String) extends BaseRepositorySpec(path) {

  val barajasAirportDbo = AirportDbo("Barajas", "MAD", "LEMD", 0L)
  val barajasUppercaseDbo = AirportDbo("BARAJAS", "MAD", "LEMD", 0L)

  val dbl = new DatabaseLayer(config) {

    import profile.api._

    val countryRepo = new CountrySlickRepository
    val airportRepo = new AirportSlickRepository

    def createSchema(): Future[Unit] = {
      config.db.run(
        (
          countries.schema ++
            airports.schema
          ).create
      )
    }

    def dropSchema(): Future[Unit] = {
      config.db.run(
        (
          countries.schema ++
            airports.schema
          ).drop
      )
    }

  }

  import dbl.executeFromDb

  behavior of "Airport Repository"

  it should "insert an airport into the database" in {
    val result = insertAirport()

    result map { tuple =>
      val (aid, _) = tuple
      assert(aid > 0)
    }
  }

  it should "fail to insert a duplicate airport intro the database" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        countryId <- dbl.countryRepo.insert(esCountryDbo)
        _ <- dbl.airportRepo.insert(
          Seq(
            barajasAirportDbo.copy(countryId = countryId),
            barajasAirportDbo.copy(countryId = countryId)
          )
        )
      } yield ()
    }
  }

  it should "fail to insert an airport into the database with a missing country" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        _ <- dbl.airportRepo.insert(
          barajasAirportDbo.copy(countryId = 0L)
        )
      } yield ()
    }
  }

  it should "update an airport from the database" in {
    val result = for {
      (aid, _) <- insertAirport()
      ukid <- dbl.countryRepo.insert(ukCountryDbo)
      _ <- dbl.airportRepo.update(barajasUppercaseDbo.copy(id = Some(aid), countryId = ukid))
      updated <- dbl.airportRepo.findById(aid)
    } yield (aid, ukid, updated)

    result map { tuple =>
      val (aid, ukid, updated) = tuple
      assert(aid > 0L)
      assert(updated == Option(barajasUppercaseDbo.copy(id = Some(aid), countryId = ukid)))
    }
  }

  it should "delete an airport from the database" in {
    val result = for {
      (aid, _) <- insertAirport()
      did <- dbl.airportRepo.delete(aid)
      count <- dbl.airportRepo.count()
    } yield (aid, did, count)

    result map { tuple =>
      val (aid, dCount, count) = tuple
      assert(aid > 0L)
      assert(dCount == 1)
      assert(count == 0)
    }
  }

  it should "find an airport by its id" in {
    val result = for {
      (aid, _) <- insertAirport()
      airport <- dbl.airportRepo.findById(aid)
    } yield (airport, aid)

    result map { tuple =>
      val (airport, id) = tuple
      assert(airport.isDefined)
      assert(airport.get.id.contains(id))
    }
  }

  it should "find an airport by its country code" in {
    val result = for {
      (aid, cid) <- insertAirport()
      airports <- dbl.airportRepo.findByCountryCode(esCountry._2) // "ES"
    } yield (airports, aid, cid)

    result map { tuple =>
      val (as, airportId, countryId) = tuple
      assert(as.size == 1)
      assert(as.head.id.contains(airportId))
      assert(as.head.countryId == countryId)
    }
  }

  it should "find an airport by its iata code" in {
    val result = for {
      _ <- insertAirport()
      airport <- dbl.airportRepo.findByIataCode(barajasAirportDbo.iataCode)
    } yield airport

    result map { airport =>
      assert(airport.isDefined)
      assert(airport.get.iataCode == barajasAirportDbo.iataCode)
    }
  }

  private def insertAirport() = for {
    countryId <- dbl.countryRepo.insert(esCountryDbo)
    airportId <- dbl.airportRepo.insert(
      barajasAirportDbo.copy(countryId = countryId)
    )
  } yield (airportId, countryId)


  override def beforeEach(): Unit = {
    Await.result(dbl.createSchema(), waitTimeout)
  }

  override def afterEach(): Unit = {
    Await.result(dbl.dropSchema(), waitTimeout)
  }

}
