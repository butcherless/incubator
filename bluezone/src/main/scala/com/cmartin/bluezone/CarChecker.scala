package com.cmartin.bluezone

import com.cmartin.bluezone.Model.DomainError
import zio.{IO, ZIO}

import java.time.{Clock, LocalDateTime}

final case class CarChecker(ticketStore: ForStoringTickets) extends ForCheckingCars {

  override def illegallyParkedCar(clock: Clock, carPlate: String, rateName: String): IO[DomainError, Boolean] = {
    val currentDateTime = LocalDateTime.now(clock)
    for {
      ticketsOfCarAndRate <- this.ticketStore.findByCarRateOrderByEndingDateTimeDesc(carPlate, rateName)
      result              <- ZIO.succeed {
                               if (ticketsOfCarAndRate.isEmpty) true
                               else {
                                 val latestEndingDateTime = ticketsOfCarAndRate.head.endingDateTime
                                 currentDateTime.isAfter(latestEndingDateTime)
                               }
                             }
    } yield result
  }
}
