package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.domain.Model.{ Airport, Country }

object Model {

  /** Database entity based on a type and an identifier
    *
    * @tparam T
    *   type for the entity
    * @tparam I
    *   identifier for the entity
    */
  trait Entity[T, I] {
    val id: I
  }

  trait BaseEntity[I] {
    val id: I
  }

  abstract class LongEntity(id: Option[Long]) extends BaseEntity[Option[Long]]

  final case class CountryDbo(
      name: String,
      code: String,
      id: Option[Long] = None
  ) extends LongEntity(id)

  object CountryDboConverter {
    def fromCountry(country: Country): CountryDbo                 = {
      CountryDbo(
        name = country.name,
        code = country.code
      )
    }
    def toModel(dbo: CountryDbo): Country                         = {
      Country(
        name = dbo.name,
        code = dbo.code
      )
    }
    def updateFrom(dbo: CountryDbo, country: Country): CountryDbo = {
      dbo.copy(name = country.name, code = country.code)
    }
  }

  final case class AirportDbo(
      name: String,
      iataCode: String,
      icaoCode: String,
      countryId: Long,
      id: Option[Long] = None
  ) extends LongEntity(id)

  object AirportDboConverter {
    def from(airport: Airport, countryId: Long): AirportDbo =
      AirportDbo(
        name = airport.name,
        iataCode = airport.iataCode,
        icaoCode = airport.icaoCode,
        countryId = countryId
      )

    def toModel(adbo: AirportDbo, cdbo: CountryDbo): Airport = {
      Airport(
        name = adbo.name,
        iataCode = adbo.iataCode,
        icaoCode = adbo.icaoCode,
        country = CountryDboConverter.toModel(cdbo)
      )
    }

    def update(dbo: AirportDbo, airport: Airport, countryId: Long): AirportDbo =
      dbo.copy(
        name = airport.name,
        iataCode = airport.iataCode,
        icaoCode = airport.icaoCode,
        countryId
      )

  }

}
