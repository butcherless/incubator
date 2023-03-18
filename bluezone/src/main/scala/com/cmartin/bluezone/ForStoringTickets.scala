package com.cmartin.bluezone

import com.cmartin.bluezone.Model.{ DomainError, Ticket }
import zio.IO

trait ForStoringTickets {
  def nextCode(): IO[DomainError, String]

  def findByCode(ticketCode: String): IO[DomainError, Ticket]

  def store(ticket: Ticket): IO[DomainError, Unit]

  def findByCarRateOrderByEndingDateTimeDesc(carPlate: String, rateName: String): IO[DomainError, List[Ticket]]

  def delete(ticketCode: String): IO[DomainError, Unit]

  def exists(ticketCode: String): IO[DomainError, Boolean]

  def setNextCode(ticketCode: String): IO[DomainError, Unit]

  def nextAvailableCode(): IO[DomainError, String]
}
