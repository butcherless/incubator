package com.cmartin.learn.application

import scala.concurrent.Future

import com.cmartin.learn.adapter.postgres.Model.CountryDboConverter
import com.cmartin.learn.adapter.postgres.SlickRepositories.DAL
import com.cmartin.learn.domain.CountryService
import com.cmartin.learn.domain.Model._

/* dbo.id.get: Access to the identifier is guaranteed by
   the Repository primary key
 */
class CountryUseCases(dal: DAL) extends CountryService {

  import dal._

  override def create(country: Country): Future[Country] = {
    for {
      dbo <- Future.successful(CountryDboConverter.fromCountry(country))
      _   <- countryRepo.insert(dbo)
    } yield country
  }

  override def update(country: Country): Future[Country] = {
    for {
      dbo <- findCountryByCode(country.code)
      _   <- countryRepo.update(CountryDboConverter.updateFrom(dbo, country))
    } yield country
  }

  override def searchByCode(code: String): Future[Country] = {
    for {
      dbo <- findCountryByCode(code)
    } yield CountryDboConverter.toModel(dbo)
  }

  override def delete(country: Country): Future[Country] = {
    for {
      dbo <- findCountryByCode(country.code)
      _   <- countryRepo.delete(dbo.id.get)
    } yield country
  }

  override def exists(country: Country): Future[Boolean] = {
    for {
      dboOption <- countryRepo.findByCode(country.code)
    } yield dboOption.isDefined
  }

  /* H E L P E R   F U N C T I O N */
  private def findCountryByCode(code: String) = {
    for {
      countryOption <- countryRepo.findByCode(code)
      country       <- checkElement(countryOption, s"country code not found: $code")
    } yield country
  }

}
