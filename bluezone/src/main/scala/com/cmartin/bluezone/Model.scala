package com.cmartin.bluezone

import java.time.{ Clock, LocalDateTime }

object Model {
  case class Ticket(
      code: String,
      carPlate: String,
      rateName: String,
      startingDateTime: LocalDateTime,
      endingDateTime: LocalDateTime,
      price: BigDecimal
  )

  case class Rate(
      name: String,
      amountPerHour: BigDecimal
  )

  case class PayRequest(
      ticketCode: String,
      paymentCard: String,
      amount: BigDecimal
  )
  case class PurchaseTicketRequest(
      carPlate: String,
      rateName: String,
      clock: Clock,
      amount: BigDecimal,
      paymentCard: String
  )

  // ERRORS
  trait DomainError {
    val message: String
  }
  case class PayErrorException(message: String)
}
