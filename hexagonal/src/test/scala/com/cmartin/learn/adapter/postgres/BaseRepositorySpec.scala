package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.adapter.postgres.Model.CountryDbo
import com.cmartin.learn.test.AviationData.Constants._
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

  val esCountryDbo               = CountryDbo(esCountry._1, esCountry._2)
  val ukCountryDbo               = CountryDbo(ukCountry._1, ukCountry._2)
  val countrySequence = Seq(esCountryDbo, ukCountryDbo)
  val spainUpperCaseDbo: CountryDbo = CountryDbo(esCountry._1.toUpperCase, esCountry._2.toUpperCase)
}
