package com.cmartin.learn.domain

import com.cmartin.learn.domain.Model.Country

import scala.concurrent.Future

trait CountryRepository extends AbstractRepository[Future, Country] {
  def findByCode(code: String): Future[Country]
}
