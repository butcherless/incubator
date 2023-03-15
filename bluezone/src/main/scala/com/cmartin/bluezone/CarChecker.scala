package com.cmartin.bluezone

import java.time.Clock
import java.time.LocalDateTime

final case class CarChecker(ticketStore: ForStoringTickets) extends ForCheckingCars {

  override def illegallyParkedCar(clock: Clock, carPlate: String, rateName: String): Boolean = {

    val ticketsOfCarAndRate = this.ticketStore.findByCarRateOrderByEndingDateTimeDesc(carPlate, rateName)

    if (ticketsOfCarAndRate.isEmpty) true
    else {
      val currentDateTime      = LocalDateTime.now(clock)
      val latestEndingDateTime = ticketsOfCarAndRate.head.endingDateTime
      currentDateTime.isAfter(latestEndingDateTime)
    }
  }

}
