package com.cmartin.learn.domain

import com.cmartin.learn.domain.Model.Country

import scala.concurrent.Future

trait CountryRepository {
  def save(country: Country): Future[Country]
}
