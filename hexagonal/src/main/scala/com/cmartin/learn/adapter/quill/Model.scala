package com.cmartin.learn.adapter.quill

import com.cmartin.learn.domain.Model._

import java.util.UUID

object Model {

  trait BaseEntity[I] {
    val id: I
  }

  abstract class LongEntity(id: Option[Long]) extends BaseEntity[Option[Long]]
  abstract class UuidEntity(id: UUID)         extends BaseEntity[UUID]

  case class RepositoryException(m: String) extends RuntimeException(m)

  case class CountryDbo(name: String, code: String, id: Option[Long] = None) extends LongEntity(id)
  object CountryDbo {
    def fromCountry(country: Country): CountryDbo =
      CountryDbo(
        name = country.name,
        code = country.code
      )

    def toModel(dbo: CountryDbo): Country =
      Country(
        name = dbo.name,
        code = dbo.code
      )

    def update(dbo: CountryDbo, country: Country): CountryDbo =
      dbo.copy(name = country.name, code = country.code)
  }

  case class AirportDbo(
      name: String,
      iataCode: String,
      icaoCode: String,
      countryId: Long,
      id: Option[Long] = None
  ) extends LongEntity(id)

  object AirportDbo {
    def from(airport: Airport, countryId: Long): AirportDbo =
      AirportDbo(
        name = airport.name,
        iataCode = airport.iataCode,
        icaoCode = airport.icaoCode,
        countryId = countryId
      )
    def toModel(dbo: AirportDbo, c: CountryDbo): Airport =
      Airport(
        name = dbo.name,
        iataCode = dbo.iataCode,
        icaoCode = dbo.icaoCode,
        country = CountryDbo.toModel(c)
      )

    def update(dbo: AirportDbo, airport: Airport, countryId: Long) =
      dbo.copy(
        name = airport.name,
        iataCode = airport.iataCode,
        icaoCode = airport.icaoCode,
        countryId
      )
  }

}
