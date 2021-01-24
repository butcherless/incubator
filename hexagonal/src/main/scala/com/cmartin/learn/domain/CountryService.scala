package com.cmartin.learn.domain
import com.cmartin.learn.domain.Model._

import scala.concurrent.Future

trait CountryService {
  def create(country: Country): Future[Country]
  def update(country: Country): Future[Country]
  def delete(country: Country): Future[Country]
  def searchByCode(code: String): Future[Country]
  def exists(country: Country): Future[Boolean]
}
