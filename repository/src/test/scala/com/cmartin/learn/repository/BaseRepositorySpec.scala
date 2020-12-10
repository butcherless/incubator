package com.cmartin.learn.repository

import com.cmartin.learn.test.Constants._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

// "h2_dc"
abstract class BaseRepositorySpec(path: String)
    extends AsyncFlatSpec
    with Matchers
    with BeforeAndAfterEach {

  val config = DatabaseConfig.forConfig[JdbcProfile](path)

  val spainCountry: Country = Country(esCountry._1, esCountry._2)
}
