package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.domain.CountryRepository
import com.cmartin.learn.domain.Model.Country

import scala.concurrent.Future

class CountryPostgresRepository extends CountryRepository {

  import Model._


  override def insert(e: Country): Future[Country] = {
    val dbo = CountryDbo.fromCountry(e)

    //TODO postgres driver ops
    // Postgres.insert(dbo)

    Future.successful(e)
  }

  override def findById(id: Long): Future[Option[Country]] = ???

  override def findByCode(code: String): Future[Country] = ???
}
