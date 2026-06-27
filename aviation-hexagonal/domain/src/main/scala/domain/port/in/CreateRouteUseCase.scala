package domain.port.in

import domain.error.DomainError
import domain.model.Route
import zio.IO

case class CreateRouteCommand(
    originIata: String,
    destinationIata: String,
    airlineIcao: String,
    distanceKm: Int
)

trait CreateRouteUseCase {
  def create(command: CreateRouteCommand): IO[DomainError, Route]
}
