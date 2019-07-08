package com.cmartin.poc.repository2

import java.time.LocalDate

import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues

import scala.concurrent.Await

class AirlineRepositorySpec extends BaseRepositorySpec with OptionValues {

  val norway = Country(noCountry._1, noCountry._2)
  //val iberia = Airline(ibkAirline._1, ibkAirline._2, )


  behavior of "Airline Repository"


  it should "insert an airline into the database" in {
    val result = for {
      cid <- repos.countryRepo.insert(norway)
      aid <- repos.airlineRepo.insert(Airline(ibkAirline._1, ibkAirline._2, cid))
    } yield aid

    result map { id =>
      assert(id == 1)
    }
  }

  it should "update an airline from the database" in {
    val updatedString = "UPDATED"
    val now = LocalDate.now()
    val result = for {
      cid <- repos.countryRepo.insert(norway)
      aid <- repos.airlineRepo.insert(Airline(ibkAirline._1, ibkAirline._2, cid))
      uid <- repos.airlineRepo.update(Airline(updatedString, now, cid, Option(aid)))
      updated <- repos.airlineRepo.findById(aid)
    } yield updated.value

    result map { airline =>
      assert(airline.name == updatedString)
      assert(airline.foundationDate == now)
    }
  }

  it should "delete an airline from the database" in {
    val result = for {
      cid <- repos.countryRepo.insert(norway)
      aid <- repos.airlineRepo.insert(Airline(ibkAirline._1, ibkAirline._2, cid))
      deleted <- repos.airlineRepo.delete(aid)
      count <- repos.airlineRepo.count
    } yield (aid, deleted, count)

    result map { tuple =>
      assert(tuple._1 == tuple._2)
      assert(tuple._3 == 0)
    }
  }

  it should "retrieve an airline from the database" in {
    val result = for {
      cid <- repos.countryRepo.insert(norway)
      aid <- repos.airlineRepo.insert(Airline(ibkAirline._1, ibkAirline._2, cid))
      airline <- repos.airlineRepo.findById(aid)
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
      cid <- repos.countryRepo.insert(spain)
      _ <- repos.airlineRepo.insert(Airline(ibsAirline._1, ibsAirline._2, cid))
      _ <- repos.airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, cid))
      count <- repos.airlineRepo.findByCountryCode(esCountry._2)
    } yield count

    result map { seq => assert(seq.size == expectedCount) }
  }


  override def beforeEach(): Unit = {
    Await.result(repos.countryRepo.create, timeout)
    Await.result(repos.airlineRepo.create, timeout)
  }

  override def afterEach(): Unit = {
    Await.result(repos.airlineRepo.drop, timeout)
    Await.result(repos.countryRepo.drop, timeout)
  }

}
