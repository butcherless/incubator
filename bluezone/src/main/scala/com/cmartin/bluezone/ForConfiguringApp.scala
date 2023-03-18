package com.cmartin.bluezone

import com.cmartin.bluezone.Model.{PayRequest, Rate, Ticket}

trait ForConfiguringApp {
  def createRates(rates: List[Rate]): Unit

  def createTicket(ticket: Ticket): Unit

  def eraseTicket(ticketCode: String): Unit

  def setNextTicketCodeToReturn(ticketCode: String): Unit

  def getNextTicketCodeToReturn(): String

  def getLastPayRequestDone(): PayRequest
}
