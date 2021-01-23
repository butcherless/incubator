package com.cmartin.learn.domain
import com.cmartin.learn.domain.Model._

import scala.concurrent.Future

trait CountryService {
  def create(country: Country): Future[Country]
  def update(country: Country): Future[Country]
}
