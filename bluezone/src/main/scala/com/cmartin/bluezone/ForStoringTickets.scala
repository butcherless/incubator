package com.cmartin.bluezone

import Model.Ticket

trait ForStoringTickets {
  def nextCode(): String

  def findByCode(ticketCode: String): Ticket

  def store(ticket: Ticket): Unit

  def findByCarRateOrderByEndingDateTimeDesc(carPlate: String, rateName: String): List[Ticket]

  def delete(ticketCode: String): Unit

  def exists(ticketCode: String): Boolean

  def setNextCode(ticketCode: String): Unit

  def nextAvailableCode(): String
}
