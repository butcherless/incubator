package com.cmartin.bluezone.hexagon

import com.cmartin.bluezone.hexagon.Model.{DomainError, PayRequest, Rate, Ticket}
import zio.{IO, ZIO}

final case class AppConfigurator(
    rateProvider: ForObtainingRates,
    ticketStore: ForStoringTickets,
    paymentService: ForPaying
) extends ForConfiguringApp {

  override def createRates(rates: List[Rate]): IO[DomainError, Unit] =
    for {
      _ <- rateProvider.empty()
      _ <- ZIO.foreachDiscard(rates)(rate =>
             ZIO.whenZIO(rateProvider.exists(rate.name))(rateProvider.addRate(rate))
           )
    } yield ()

  override def createTicket(ticket: Ticket): IO[DomainError, Unit] =
    ZIO
      .whenZIO(ticketStore.exists(ticket.code))(ticketStore.store(ticket))
      .as(())

  override def eraseTicket(ticketCode: String): IO[DomainError, Unit] =
    ZIO
      .whenZIO(ticketStore.exists(ticketCode))(ticketStore.delete(ticketCode))
      .as(())

  override def setNextTicketCodeToReturn(ticketCode: String): IO[DomainError, Unit] =
    ticketStore.setNextCode(ticketCode)

  override def getNextTicketCodeToReturn(): IO[DomainError, String] =
    ticketStore.nextAvailableCode()

  override def getLastPayRequestDone(): IO[DomainError, PayRequest] =
    paymentService.lastPayRequest()
}
