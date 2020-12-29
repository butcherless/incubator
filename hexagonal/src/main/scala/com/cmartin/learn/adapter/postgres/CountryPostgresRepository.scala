package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.domain.CountryRepository
import com.cmartin.learn.domain.Model.Country

import scala.concurrent.Future

class CountryPostgresRepository extends CountryRepository {

  import Model._

  override def save(country: Country): Future[Country] = {
    val dbo = CountryDbo.fromCountry(country)

    //TODO postgres driver ops
    // Postgres.insert(dbo)

    Future.successful(country)
  }
}
