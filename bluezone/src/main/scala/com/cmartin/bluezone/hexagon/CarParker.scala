package com.cmartin.bluezone.hexagon

import com.cmartin.bluezone.hexagon.Model._
import zio.{ IO, UIO, ZIO }

import java.time.LocalDateTime

final case class CarParker(
    rateProvider: ForObtainingRates,
    ticketStore: ForStoringTickets,
    paymentService: ForPaying
) extends ForParkingCars {

  override def getAllRatesByName(): IO[DomainError, Map[String, Rate]] =
    rateProvider
      .findAll()
      .flatMap(rateSetToMap)

  override def purchaseTicket(purchaseTicketRequest: PurchaseTicketRequest): IO[DomainError, String] = {
    val paymentCard = purchaseTicketRequest.paymentCard
    val moneyToPay  = purchaseTicketRequest.amount
    val rateName    = purchaseTicketRequest.rateName
    val starting    = LocalDateTime.now(purchaseTicketRequest.clock)
    val carPlate    = purchaseTicketRequest.carPlate
    for {
      ticketCode     <- this.ticketStore.nextCode()
      _              <- paymentService.pay(PayRequest(ticketCode, paymentCard, moneyToPay))
      rateCalculator <- this.rateProvider.findByName(rateName).map(RateCalculator)
      ending         <- rateCalculator.getUntilGivenAmount(starting, moneyToPay)
      _              <- this.ticketStore.store(Ticket(ticketCode, carPlate, rateName, starting, ending, moneyToPay))
    } yield ticketCode
  }

  override def getTicket(ticketCode: String): IO[DomainError, Ticket] =
    ticketStore.findByCode(ticketCode)

  private def rateSetToMap(rates: Set[Rate]): UIO[Map[String, Rate]] =
    ZIO.succeed(rates.map(rate => (rate.name, rate)).toMap)
}
