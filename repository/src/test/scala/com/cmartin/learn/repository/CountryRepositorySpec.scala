package com.cmartin.learn.repository

import java.sql.SQLIntegrityConstraintViolationException

import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues

import scala.concurrent.{Await, Future}

class CountryRepositorySpec
  extends BaseRepositorySpec
    with OptionValues {

  val spainUpperCase = Country(esCountry._1.toUpperCase, esCountry._2.toUpperCase)
  val unitedKingdom = Country(ukCountry._1, ukCountry._2)
  val countrySequence: Seq[Country] = Seq(spainCountry, unitedKingdom)


  val dal = new DatabaseAccessLayer2(config) {

    import profile.api._

    val countryRepo = new CountryRepository

    def createSchema(): Future[Unit] = {
      config.db.run(countries.schema.create)
    }

    def dropSchema(): Future[Unit] = {
      config.db.run(countries.schema.drop)
    }

  }

  "Country Repository" should "insert a country into the database" in {
    val result = dal.countryRepo.insert(spainCountry)

    result map { id => assert(id > 0) }
  }

  it should "fail to insert a duplicate country intro the database" in {
    recoverToSucceededIf[SQLIntegrityConstraintViolationException] {
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
      cid <- dal.countryRepo.insert(spainCountry)
      uid <- dal.countryRepo.update(spainUpperCase.copy(id = Option(cid)))
      updated <- dal.countryRepo.findById(cid)
    } yield (cid, uid, updated)

    result map { tuple =>
      assert(tuple._1 > 0L)
      assert(tuple._1 == tuple._2)
      assert(tuple._3 == Option(spainUpperCase.copy(id = Option(tuple._1))))
    }
  }

  it should "delete a country from the database" in {
    val result = for {
      cid <- dal.countryRepo.insert(spainCountry)
      did <- dal.countryRepo.delete(cid)
      count <- dal.countryRepo.count()
    } yield (cid, did, count)

    result map { tuple =>
      assert(tuple._1 > 0L)
      assert(tuple._3 == 0)
    }
  }

  it should "find a country by its code" in {
    val result = for {
      _ <- dal.countryRepo.insert(spainCountry)
      country <- dal.countryRepo.findByCode(spainCountry.code)
    } yield country.value

    result map { country => assert(country.code == spainCountry.code) }
  }

  it should "retrieve all countries from the database" in {
    val result: Future[Seq[Country]] = for {
      _ <- dal.countryRepo.insert(countrySequence)
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

    result map { tuple =>
      assert(tuple._1.size == tuple._2.size)
      assert(tuple._1.size == tuple._3)
    }
  }


  override def beforeEach(): Unit = {
    Await.result(dal.createSchema(), timeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.dropSchema(), timeout)
  }
}
