package com.cmartin.bluezone

import com.cmartin.bluezone.Model.{ DomainError, PayRequest, Rate, Ticket }
import zio.IO
trait ForConfiguringApp {
  def createRates(rates: List[Rate]): IO[DomainError, Unit]

  def createTicket(ticket: Ticket): IO[DomainError, Unit]

  def eraseTicket(ticketCode: String): IO[DomainError, Unit]

  def setNextTicketCodeToReturn(ticketCode: String): IO[DomainError, Unit]

  def getNextTicketCodeToReturn(): IO[DomainError, String]

  def getLastPayRequestDone(): IO[DomainError, PayRequest]
}
