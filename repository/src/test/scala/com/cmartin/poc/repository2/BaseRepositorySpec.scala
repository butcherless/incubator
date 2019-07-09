package com.cmartin.poc.repository2

import com.cmartin.learn.test.Constants._
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterEach, Matchers}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.duration._


abstract class BaseRepositorySpec
  extends AsyncFlatSpec
    with Matchers
    with BeforeAndAfterEach {

  val config = DatabaseConfig.forConfig[JdbcProfile]("h2_dc")

  //TODO remove, one DAL per test
  val repos = new DatabaseAccessLayer(config.profile) {
    val countryRepo = new CountryRepository(config.db)
    val airlineRepo = new AirlineRepository(config.db)
    val aircraftRepo = new AircraftRepository(config.db)
  }

  val timeout = 2 seconds

  val spain = Country(esCountry._1, esCountry._2)

}
