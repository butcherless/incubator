package com.cmartin.learn.adapter.quill

import com.cmartin.learn.adapter.quill.QuillRepositoryCommons.AbstractDboRepository
import com.cmartin.learn.domain.Model._
import com.cmartin.learn.domain.PersistencePorts._

import scala.concurrent.{ ExecutionContext, Future }

class CountryQuillRepository(configPrefix: String)(implicit ec: ExecutionContext)
    extends AbstractDboRepository(configPrefix)
    with CountryRepository {

  import Model._
  import ctx._

  override def findByCode(code: String): Future[Country] = {
    val program = for {
      dbos    <- runIO(findCountryByCodeQuery(code))
      dbo     <- checkHeadElement(dbos, s"country code not found: $code")
      country <- IO.successful(CountryDbo.toModel(dbo))
    } yield country

    performIO(program)
  }

  def insert(country: Country): Future[Country] = {
    val dbo     = CountryDbo.fromCountry(country)
    val program =
      for {
        _ <- runIO(insertQuery(dbo))
      } yield country

    performIO(program)
  }

  def update(country: Country): Future[Country] = {
    val program =
      for {
        dbos <- runIO(findCountryByCodeQuery(country.code))
        dbo  <- checkHeadElement(dbos, s"country code not found: ${country.code}}")
        _    <- runIO(updateQuery(CountryDbo.update(dbo, country)))
      } yield country

    performIO(program)
  }

  def delete(country: Country): Future[Country] = {
    val program = for {
      dbos <- runIO(findCountryByCodeQuery(country.code))
      dbo  <- checkHeadElement(dbos, s"country code not found: ${country.code})")
      _    <- runIO(deleteQuery(dbo))
    } yield country

    performIO(program)
  }

}
