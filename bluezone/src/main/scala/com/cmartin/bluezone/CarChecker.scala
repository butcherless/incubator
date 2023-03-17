package com.cmartin.bluezone

import java.time.Clock
import java.time.LocalDateTime
import zio.{IO, ZIO}
import Model.DomainError

final case class CarChecker(ticketStore: ForStoringTickets) extends ForCheckingCars {

  override def illegallyParkedCar(clock: Clock, carPlate: String, rateName: String): IO[DomainError, Boolean] = {

    val ticketsOfCarAndRate = this.ticketStore.findByCarRateOrderByEndingDateTimeDesc(carPlate, rateName)

    if (ticketsOfCarAndRate.isEmpty) ZIO.succeed(true)
    else {
      val currentDateTime      = LocalDateTime.now(clock)
      val latestEndingDateTime = ticketsOfCarAndRate.head.endingDateTime
      ZIO.succeed(currentDateTime.isAfter(latestEndingDateTime))
    }
  }

}
