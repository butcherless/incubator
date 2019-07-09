package com.cmartin.poc.repository2

import org.scalatest.OptionValues
import com.cmartin.learn.test.Constants._

import scala.concurrent.{Await, Future}

class CountryRepositorySpec extends BaseRepositorySpec with OptionValues {

  val spainUpperCase = Country(esCountry._1.toUpperCase, esCountry._2.toUpperCase)
  val unitedKingdom = Country(ukCountry._1, ukCountry._2)
  val countrySequence: Seq[Country] = Seq(spain, unitedKingdom)

  val dal = new DatabaseAccessLayer2(config) {
    val countryRepo = new CountryRepository(config.db)
  }

  behavior of "Country Repository"

  it should "insert a country into the database" in {
    val result = dal.countryRepo.insert(spain)

    result map { id => assert(id == 1) }
  }

  it should "find a country by its code" in {
    val result = for {
      _ <- dal.countryRepo.insert(spain)
      country <- dal.countryRepo.findByCode(spain.code)
    } yield country.value

    result map { country => assert(country.code == spain.code) }
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
      cid <- dal.countryRepo.insert(spain)
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
      cid <- dal.countryRepo.insert(spain)
      did <- dal.countryRepo.delete(cid)
      count <- dal.countryRepo.count()
    } yield (cid, did, count)

    result map { tuple =>
      assert(tuple._1 == tuple._2)
      assert(tuple._3 == 0)
    }
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

  override def beforeEach(): Unit = {
    Await.result(dal.countryRepo.create, timeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.countryRepo.drop, timeout)
  }
}
