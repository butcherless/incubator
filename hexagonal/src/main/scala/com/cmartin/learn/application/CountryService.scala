package com.cmartin.learn.application

import com.cmartin.learn.domain.CountryRepository
import com.cmartin.learn.domain.Model.Country

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CountryService(countryRepository: CountryRepository) {
  def create(country: Country): Future[Country] = {
    for {
      _ <- countryRepository.save(country)
    } yield country
  }
}
