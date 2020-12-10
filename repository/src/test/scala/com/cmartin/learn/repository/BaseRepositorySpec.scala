package com.cmartin.learn.repository

import com.cmartin.learn.test.Constants._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.duration._

abstract class BaseRepositorySpec extends AsyncFlatSpec with Matchers with BeforeAndAfterEach {
  val config = DatabaseConfig.forConfig[JdbcProfile]("h2_dc")

  val spainCountry: Country = Country(esCountry._1, esCountry._2)
}
