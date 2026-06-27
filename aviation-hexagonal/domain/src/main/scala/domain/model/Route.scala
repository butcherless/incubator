package domain.model

import java.util.UUID

opaque type RouteId = UUID

object RouteId {
  def apply(value: UUID): RouteId        = value
  def generate: RouteId                  = UUID.randomUUID()
  extension (r: RouteId) def value: UUID = r
}

case class Route(
    id: RouteId,
    origin: IataCode,
    destination: IataCode,
    airlineIcao: IcaoCode,
    distanceKm: Int
)
