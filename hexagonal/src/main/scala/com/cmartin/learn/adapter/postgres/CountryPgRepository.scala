package com.cmartin.learn.adapter.postgres
import com.cmartin.learn.adapter.postgres.Abstractions.CountryContext
import com.cmartin.learn.adapter.postgres.Model.CountryDbo
import com.cmartin.learn.domain.CountryRepository
import com.cmartin.learn.domain.Model.Country
import io.getquill.{Literal, PostgresAsyncContext, PostgresDialect}

import scala.concurrent.{ExecutionContext, Future}

class CountryPgRepository(configPrefix: String)(implicit ec: ExecutionContext)
    extends PostgresAsyncContext[Literal](Literal, configPrefix)
    with CountryContext[PostgresDialect, Literal]
    with CountryRepository {

  override def findByCode(code: String): Future[Country] = {
    for {
      dbos <- run(findByCode_(code))
      country <- dbos.headOption
        .fold(throw new RuntimeException("not-found"))(dbo => //TODO define exception
          Future.successful(CountryDbo.toModel(dbo))
        )
    } yield country
  }

  override def insert(country: Country): Future[Country] = {
    val dbo = CountryDbo.fromCountry(country)
    for {
      _       <- run(insert_(dbo))
      country <- Future.successful(country)
    } yield country
  }

  override def findById(id: Long): Future[Option[Country]] = ???
}
