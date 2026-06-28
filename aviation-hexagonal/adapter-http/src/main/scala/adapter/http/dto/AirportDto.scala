package adapter.http.dto

import domain.model.Airport
import sttp.tapir.Schema
import sttp.tapir.Validator

case class AirportDto(iata: String, name: String, city: String, countryCode: String)

object AirportDto {
  def fromDomain(airport: Airport): AirportDto =
    AirportDto(
      iata = airport.iata.value,
      name = airport.name,
      city = airport.city,
      countryCode = airport.countryCode.value
    )

  given Schema[AirportDto] = Schema.derived[AirportDto]
    .modify(_.iata)(
      _.description("3-letter IATA airport code.")
        .validate(Validator.minLength(3))
        .validate(Validator.maxLength(3))
    )
    .modify(_.name)(_.description("Full airport name."))
    .modify(_.city)(_.description("City served by the airport."))
    .modify(_.countryCode)(
      _.description("ISO 3166-1 alpha-2 country code.")
        .validate(Validator.minLength(2))
        .validate(Validator.maxLength(2))
    )
}
