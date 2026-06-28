package adapter.http.dto

import domain.model.Route
import sttp.tapir.Schema
import sttp.tapir.Validator

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

  given Schema[RouteDto] = Schema.derived[RouteDto]
    .modify(_.id)(_.description("Unique route identifier.").format("uuid"))
    .modify(_.originIata)(
      _.description("IATA code of the origin airport.")
        .validate(Validator.minLength(3))
        .validate(Validator.maxLength(3))
    )
    .modify(_.destinationIata)(
      _.description("IATA code of the destination airport.")
        .validate(Validator.minLength(3))
        .validate(Validator.maxLength(3))
    )
    .modify(_.airlineIcao)(
      _.description("ICAO code of the operating airline.")
        .validate(Validator.minLength(3))
        .validate(Validator.maxLength(3))
    )
    .modify(_.distanceKm)(_.description("Flight distance in kilometres.").validate(Validator.min(1)))
}

object CreateRouteRequest {
  given Schema[CreateRouteRequest] = Schema.derived[CreateRouteRequest]
    .modify(_.originIata)(
      _.description("IATA code of the origin airport.")
        .validate(Validator.minLength(3))
        .validate(Validator.maxLength(3))
    )
    .modify(_.destinationIata)(
      _.description("IATA code of the destination airport.")
        .validate(Validator.minLength(3))
        .validate(Validator.maxLength(3))
    )
    .modify(_.airlineIcao)(
      _.description("ICAO code of the operating airline.")
        .validate(Validator.minLength(3))
        .validate(Validator.maxLength(3))
    )
    .modify(_.distanceKm)(_.description("Flight distance in kilometres.").validate(Validator.min(1)))
}
