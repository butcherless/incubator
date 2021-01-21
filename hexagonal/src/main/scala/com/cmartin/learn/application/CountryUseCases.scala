package com.cmartin.learn.application

import com.cmartin.learn.adapter.postgres.Model.CountryDboConverter
import com.cmartin.learn.adapter.postgres.SlickRepositories.DAL
import com.cmartin.learn.domain.CountryService
import com.cmartin.learn.domain.Model._

import scala.concurrent.Future

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
      dboOption <- dal.countryRepo.findByCode(country.code)
      dbo       <- checkElement(dboOption, s"country code not found: ${country.code}")
      _         <- countryRepo.update(CountryDboConverter.updateFrom(dbo, country))
    } yield country
  }
}
