package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.adapter.postgres.Model.AirportDbo
import com.cmartin.learn.adapter.postgres.SlickRepositories.DatabaseLayer
import com.cmartin.learn.test.AviationData.Constants.waitTimeout

import scala.concurrent.{Await, Future}

abstract class AirportRepositorySpec(path: String) extends BaseRepositorySpec(path) {

  val barajasAirportDbo = AirportDbo("Barajas", "MAD", "LEMD", 0L)

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
    val result = for {
      countryId <- dbl.countryRepo.insert(esCountryDbo)
      airport <- dbl.airportRepo.insert(
        barajasAirportDbo.copy(countryId = countryId)
      )
    } yield airport

    result map { id =>
      assert(id > 0)
    }
  }

  it should "fail to insert a duplicate airport intro the database" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        countryId <- dbl.countryRepo.insert(esCountryDbo)
        _ <- dbl.airportRepo.insert(
          barajasAirportDbo.copy(countryId = countryId)
        )
        _ <- dbl.airportRepo.insert(
          barajasAirportDbo.copy(countryId = countryId)
        )
      } yield ()
    }
  }

  override def beforeEach(): Unit = {
    Await.result(dbl.createSchema(), waitTimeout)
  }

  override def afterEach(): Unit = {
    Await.result(dbl.dropSchema(), waitTimeout)
  }

}
