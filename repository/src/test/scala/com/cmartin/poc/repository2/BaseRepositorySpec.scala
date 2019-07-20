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

  val timeout = 2 seconds //TODO refactor test module

  val spainCountry = Country(esCountry._1, esCountry._2)

}
