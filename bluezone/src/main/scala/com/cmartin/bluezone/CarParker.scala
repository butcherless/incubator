package com.cmartin.bluezone

import Model.{PayRequest, Rate, Ticket}
import java.time.LocalDateTime

final case class CarParker(
    rateProvider: ForObtainingRates,
    ticketStore: ForStoringTickets,
    paymentService: ForPaying
) extends ForParkingCars {

  override def getAllRatesByName(): Map[String, Rate] =
    rateProvider
      .findAll()
      .map(rate => (rate.name, rate))
      .toMap

  override def purchaseTicket(purchaseTicketRequest: Model.PurchaseTicketRequest): String = {
// Pay
    val ticketCode     = this.ticketStore.nextCode()
    val paymentCard    = purchaseTicketRequest.paymentCard
    val moneyToPay     = purchaseTicketRequest.amount
    val payRequest     = PayRequest(ticketCode, paymentCard, moneyToPay)
    paymentService.pay(payRequest)
    // Calc ending date-time
    val rateName       = purchaseTicketRequest.rateName
    val rate           = this.rateProvider.findByName(rateName)
    val rateCalculator = RateCalculator(rate)
    val clock          = purchaseTicketRequest.clock
    val starting       = LocalDateTime.now(clock)
    val ending         = rateCalculator.getUntilGivenAmount(starting, moneyToPay)
    // Store
    val carPlate       = purchaseTicketRequest.carPlate;
    val ticket         = Ticket(ticketCode, carPlate, rateName, starting, ending, moneyToPay);
    this.ticketStore.store(ticket);
    ticketCode
  }

  override def getTicket(ticketCode: String): Ticket =
    ticketStore.findByCode(ticketCode)

}
