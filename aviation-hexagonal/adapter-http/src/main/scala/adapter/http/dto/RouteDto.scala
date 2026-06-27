package adapter.http.dto

import domain.model.Route

case class RouteDto(
    id: String,
    originIata: String,
    destinationIata: String,
    airlineIcao: String,
    distanceKm: Int
)

case class CreateRouteRequest(
    originIata: String,
    destinationIata: String,
    airlineIcao: String,
    distanceKm: Int
)

object RouteDto {
  def fromDomain(route: Route): RouteDto =
    RouteDto(
      id = route.id.value.toString,
      originIata = route.origin.value,
      destinationIata = route.destination.value,
      airlineIcao = route.airlineIcao.value,
      distanceKm = route.distanceKm
    )
}
