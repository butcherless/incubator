package com.cmartin.learn.domain
import scala.concurrent.Future

import Model._

trait CountryService {
  def create(country: Country): Future[Country]
  def update(country: Country): Future[Country]
}
