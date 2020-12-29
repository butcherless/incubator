package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.domain.Model.{Airport, Country}

object Model {

  /** Database entity based on a type and an identifier
    *
    * @tparam T type for the entity
    * @tparam I identifier for the entity
    */
  trait Entity[T, I] {
    val id: Option[I]
  }

  final case class CountryDbo(
      name: String,
      code: String,
      id: Option[Long] = None
  ) extends Entity[CountryDbo, Long]

  final case class AirportDbo(
      name: String,
      iataCode: String,
      icaoCode: String,
      countryId: Long,
      id: Option[Long] = None
  ) extends Entity[AirportDbo, Long]

  object CountryDbo {
    def fromCountry(country: Country): CountryDbo =
      CountryDbo(
        name = country.name,
        code = country.code
      )
  }

  object AirportDbo {
    def from(airport: Airport): AirportDbo =
      AirportDbo(
        name = airport.name,
        iataCode = airport.iataCode,
        icaoCode = airport.icaoCode,
        countryId = airport.countryId
      )
  }

}
