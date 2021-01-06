package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.domain.Model.{Airport, Country}
import io.getquill._

object Model {

  /** Database entity based on a type and an identifier
    *
    * @tparam T type for the entity
    * @tparam I identifier for the entity
    */
  trait Entity[T, I] {
    val id: I
  }

  // remove.begin
  abstract  class BaseEntity(id:Long = 0L)
  extends Entity[BaseEntity,Long]

   case class Country2Dbo(name:String, code:String,   id:Long) extends BaseEntity()


  // remove.end

  final case class CountryDbo(
      name: String,
      code: String,
      id: Long = 0L
  ) extends Entity[CountryDbo, Long]

  final case class AirportDbo(
      name: String,
      iataCode: String,
      icaoCode: String,
      countryId: Long,
      id: Long = 0L
  ) extends Entity[AirportDbo, Long]

  object CountryDbo {
    def fromCountry(country: Country): CountryDbo =
      CountryDbo(
        name = country.name,
        code = country.code,
      )

    def toModel(dbo: CountryDbo): Country =
      Country(
        name=dbo.name,
        code=dbo.code
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
