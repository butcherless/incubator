package com.cmartin.learn.common

import com.cmartin.learn.adapter.postgres.SlickRepositories.DatabaseLayer
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

object DatabaseUtils {
  val h2Config = DatabaseConfig.forConfig[JdbcProfile]("h2_dc")

  class H2Dal extends DatabaseLayer(h2Config) {
    import profile.api._

    val countryRepo = new CountrySlickRepository

    def createSchema(): Future[Unit] = {
      config.db.run(
        countries.schema.create
      )
    }
    def dropSchema(): Future[Unit]   = {
      config.db.run(
        countries.schema.drop
      )
    }
  }

  val h2Dal = new H2Dal

}
