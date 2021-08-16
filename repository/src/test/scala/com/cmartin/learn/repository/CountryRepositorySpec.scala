package com.cmartin.learn.repository

import com.cmartin.learn.test.AviationData.Constants._
import org.scalatest.OptionValues

import scala.concurrent.{Await, Future}

abstract class CountryRepositorySpec(path: String) extends BaseRepositorySpec(path) with OptionValues {

  val spainUpperCase: Country       = Country(esCountry._1.toUpperCase, esCountry._2.toUpperCase)
  val unitedKingdom: Country        = Country(ukCountry._1, ukCountry._2)
  val countrySequence: Seq[Country] = Seq(spainCountry, unitedKingdom)

  val dal = new DatabaseLayer(config) {
    import profile.api._

    val countryRepo = new CountryRepository

    def createSchema(): Future[Unit] = {
      config.db.run(countries.schema.create)
    }

    def dropSchema(): Future[Unit] = {
      config.db.run(countries.schema.drop)
    }
  }

  import dal.executeFromDb

  behavior of "Country Repository"

  it should "insert a country into the database" in {
    val result = dal.countryRepo.insert(spainCountry)

    result map { id =>
      assert(id > 0)
    }
  }

  it should "fail to insert a duplicate country intro the database" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        _ <- dal.countryRepo.insert(spainCountry)
        _ <- dal.countryRepo.insert(spainCountry)
      } yield ()
    }
  }

  it should "insert a sequence of countries into the database" in {
    val actual: Future[Seq[Long]] = for {
      cs <- dal.countryRepo.insert(countrySequence)
    } yield cs

    actual map { cs =>
      assert(cs.nonEmpty)
      assert(cs.size == countrySequence.size)
      assert(cs.forall(_ > 0L))
    }
  }

  it should "update a country from the database" in {
    val result = for {
      cid     <- dal.countryRepo.insert(spainCountry)
      uid     <- dal.countryRepo.update(spainUpperCase.copy(id = Option(cid)))
      updated <- dal.countryRepo.findById(cid)
    } yield (cid, uid, updated)

    result map { case (cid, uid, updated) =>
      assert(cid > 0L)
      assert(cid == uid)
      assert(updated == Option(spainUpperCase.copy(id = Option(cid))))
    }
  }

  it should "delete a country from the database" in {
    val result = for {
      cid   <- dal.countryRepo.insert(spainCountry)
      did   <- dal.countryRepo.delete(cid)
      count <- dal.countryRepo.count()
    } yield (cid, did, count)

    result map { case (cid, did, count) =>
      assert(cid > 0L)
      assert(did == 1)
      assert(count == 0)
    }
  }

  it should "find a country by its code" in {
    val result = for {
      _       <- dal.countryRepo.insert(spainCountry)
      country <- dal.countryRepo.findByCode(spainCountry.code)
    } yield country.value

    result map { country =>
      assert(country.code == spainCountry.code)
    }
  }

  it should "retrieve all countries from the database" in {
    val result: Future[Seq[Country]] = for {
      _  <- dal.countryRepo.insert(countrySequence)
      cs <- dal.countryRepo.findAll()
    } yield cs

    result map { seq =>
      assert(seq.size == 2)
    }
  }

  it should "delete all countries from the database" in {
    val result = for {
      cs <- dal.countryRepo.insert(countrySequence)
      fs <- dal.countryRepo.findAll()
      ds <- dal.countryRepo.deleteAll()
    } yield (cs, fs, ds)

    result map { case (cs, fs, ds) =>
      assert(cs.size == fs.size)
      assert(cs.size == ds)
    }
  }

  override def beforeEach(): Unit = {
    Await.result(dal.createSchema(), waitTimeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.dropSchema(), waitTimeout)
  }
}
