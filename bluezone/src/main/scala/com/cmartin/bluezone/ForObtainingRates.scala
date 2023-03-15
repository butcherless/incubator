package com.cmartin.bluezone

import Model.Rate

trait ForObtainingRates {
  def findAll(): Set[Rate]

  def findByName(rateName: String): Rate

  def addRate(rate: Rate): Unit

  def exists(rateName: String): Boolean

  def empty(): Unit
}
