package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.adapter.postgres.Model.CountryDbo
import com.cmartin.learn.adapter.postgres.SlickRepositories.DatabaseLayer
import com.cmartin.learn.test.AviationData.Constants._

import scala.concurrent.{Await, Future}

abstract class CountryRepositorySpec(path: String) extends BaseRepositorySpec(path) {

  val dbl = new DatabaseLayer(config) {
    import profile.api._

    val countryRepo = new CountrySlickRepository

    def createSchema(): Future[Unit] = {
      config.db.run(
        countries.schema.create
      )
    }
    def dropSchema(): Future[Unit] = {
      config.db.run(
        countries.schema.drop
      )
    }

  }

  import dbl.executeFromDb

  behavior of "Country Repository"

  it should "insert a country into the database" in {
    val result: Future[Long] = dbl.countryRepo.insert(esCountryDbo)

    result map { id =>
      assert(id > 0)
    }
  }

  it should "fail to insert a duplicate country intro the database" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        _ <- dbl.countryRepo.insert(esCountryDbo)
        _ <- dbl.countryRepo.insert(esCountryDbo)
      } yield ()
    }
  }

  it should "insert a sequence of countries into the database" in {
    val result: Future[Seq[Long]] = for {
      cs <- dbl.countryRepo.insert(countrySequence)
    } yield cs

    result map { cs =>
      assert(cs.nonEmpty)
      assert(cs.size == countrySequence.size)
      assert(cs.forall(_ > 0L))
    }
  }

  it should "update a country from the database" in {
    val result = for {
      cid     <- dbl.countryRepo.insert(esCountryDbo)
      _       <- dbl.countryRepo.update(spainUpperCaseDbo.copy(id = Option(cid)))
      updated <- dbl.countryRepo.findById(cid)
    } yield (cid, updated)

    result map { tuple =>
      val (cid, updated) = tuple
      assert(cid > 0L)
      assert(updated == Option(spainUpperCaseDbo.copy(id = Option(cid))))
    }
  }

  it should "delete a country from the database" in {
    val result = for {
      cid   <- dbl.countryRepo.insert(esCountryDbo)
      did   <- dbl.countryRepo.delete(cid)
      count <- dbl.countryRepo.count()
    } yield (cid, did, count)

    result map { tuple =>
      val (cid, dCount, count) = tuple
      assert(cid > 0L)
      assert(dCount == 1)
      assert(count == 0)
    }
  }

  it should "delete all countries from the database" in {
    val result = for {
      cs <- dbl.countryRepo.insert(countrySequence)
      fs <- dbl.countryRepo.findAll()
      ds <- dbl.countryRepo.deleteAll()
    } yield (cs, fs, ds)

    result map { tuple =>
      val (cs, fs, ds) = tuple
      assert(cs.size == fs.size)
      assert(cs.size == ds)
    }
  }

  it should "find a country by its code" in {
    val result = for {
      _       <- dbl.countryRepo.insert(esCountryDbo)
      country <- dbl.countryRepo.findByCode(esCountryDbo.code)
    } yield country

    result map { country =>
      assert(country.isDefined)
      assert(country.get.code == esCountryDbo.code)
    }
  }

  it should "retrieve all countries from the database" in {
    val result: Future[Seq[CountryDbo]] = for {
      _  <- dbl.countryRepo.insert(countrySequence)
      cs <- dbl.countryRepo.findAll()
    } yield cs

    result map { seq =>
      assert(seq.size == 2)
    }
  }

  override def beforeEach(): Unit = {
    Await.result(dbl.createSchema(), waitTimeout)
  }

  override def afterEach(): Unit = {
    Await.result(dbl.dropSchema(), waitTimeout)
  }

}
