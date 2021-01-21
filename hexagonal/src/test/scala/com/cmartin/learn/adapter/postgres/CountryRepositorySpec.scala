package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.adapter.postgres.Model.CountryDbo
import com.cmartin.learn.adapter.postgres.SlickRepositories.DatabaseLayer
import com.cmartin.learn.test.AviationData.Constants._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{Await, Future}

class CountryRepositorySpec extends AsyncFlatSpec with Matchers with BeforeAndAfterEach {

  val spainCountryDbo               = CountryDbo(esCountry._1, esCountry._2)
  val spainUpperCaseDbo: CountryDbo = CountryDbo(esCountry._1.toUpperCase, esCountry._2.toUpperCase)

  val config = DatabaseConfig.forConfig[JdbcProfile]("h2_dc")
  val dbl = new DatabaseLayer(config) {
    import profile.api._

    val countryRepo = new CountrySlickRepository

    def createSchema(): Future[Unit] = {
      config.db.run(countries.schema.create)
    }
    def dropSchema(): Future[Unit] = {
      config.db.run(countries.schema.drop)
    }

  }

  import dbl.executeFromDb

  behavior of "Country Repository"

  it should "insert a country into the database" in {
    val result: Future[Long] = dbl.countryRepo.insert(spainCountryDbo)

    result map { id =>
      assert(id > 0)
    }
  }

  it should "fail to insert a duplicate country intro the database" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        _ <- dbl.countryRepo.insert(spainCountryDbo)
        _ <- dbl.countryRepo.insert(spainCountryDbo)
      } yield ()
    }
  }

  it should "update a country from the database" in {
    val result = for {
      cid     <- dbl.countryRepo.insert(spainCountryDbo)
      uid     <- dbl.countryRepo.update(spainUpperCaseDbo.copy(id = Option(cid)))
      updated <- dbl.countryRepo.findById(cid)
    } yield (cid, uid, updated)

    result map { tuple =>
      assert(tuple._1 > 0L)
      assert(tuple._1 == tuple._2)
      assert(tuple._3 == Option(spainUpperCaseDbo.copy(id = Option(tuple._1))))
    }
  }

  override def beforeEach(): Unit = {
    Await.result(dbl.createSchema(), waitTimeout)
  }

  override def afterEach(): Unit = {
    Await.result(dbl.dropSchema(), waitTimeout)
  }

}
