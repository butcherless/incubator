package com.cmartin.learn.repository

import java.time.LocalDate

import com.cmartin.learn.repository.slick3._
import org.scalatest.OptionValues._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global

class AirlineSpec extends EntitySpec {

  val tableList = List(
    TableQuery[Countries],
    TableQuery[Airlines]
  )

  it should "retrieve an airline from the database" in new Repos {

    val airlineOption = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airlineId <- airlineRepo.insert(Airline(ibsAirline._1, ibsAirline._2, countryId))
      airline <- airlineRepo.findById(airlineId)
    } yield airline

    val airline: Option[Airline] = airlineOption.futureValue

    airline.value.id.value should be > 0L
    airline.value.name shouldBe ibsAirline._1
    airline.value.foundationDate shouldBe ibsAirline._2
  }


  trait Repos {
    val airlineRepo = new AirlineRepository
    val countryRepo = new CountryRepository
  }

  /*
    _____         _     ____        _
   |_   _|__  ___| |_  |  _ \  __ _| |_ __ _
     | |/ _ \/ __| __| | | | |/ _` | __/ _` |
     | |  __/\__ \ |_  | |_| | (_| | || (_| |
     |_|\___||___/\__| |____/ \__,_|\__\__,_|

   */

  val esCountry = ("Spain", "ES")
  val aeaAirline = ("Air Europa", LocalDate.of(1986, 11, 21))
  val ibsAirline = ("Iberia Express", LocalDate.of(2011, 10, 6))
  val ibkAirline = ("Norwegian Air International", LocalDate.of(1993, 1, 22))


}
