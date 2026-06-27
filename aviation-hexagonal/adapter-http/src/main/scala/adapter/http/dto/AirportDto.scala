package adapter.http.dto

import domain.model.Airport

case class AirportDto(iata: String, name: String, city: String, countryCode: String)

object AirportDto {
  def fromDomain(airport: Airport): AirportDto =
    AirportDto(
      iata = airport.iata.value,
      name = airport.name,
      city = airport.city,
      countryCode = airport.countryCode.value
    )
}
