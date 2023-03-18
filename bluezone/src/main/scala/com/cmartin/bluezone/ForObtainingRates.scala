package com.cmartin.bluezone

import com.cmartin.bluezone.Model.{DomainError, Rate}
import zio.IO

trait ForObtainingRates {
  def findAll(): IO[DomainError, Set[Rate]]

  def findByName(rateName: String): IO[DomainError, Rate]

  def addRate(rate: Rate): IO[DomainError, Unit]

  def exists(rateName: String): IO[DomainError, Boolean]

  def empty(): IO[DomainError, Unit]
}
