package com.cmartin.poc.repository2

import java.time.LocalDate

/**
  * Database entity based on a type and an identifier
  *
  * @tparam T type for the entity
  * @tparam I identifier for the entity
  */
trait Entity[T, I] {
  val id: Option[I]
}


final case class Country(name: String,
                         code: String,
                         id: Option[Long] = None
                        ) extends Entity[Country, Long]

final case class Aircraft(typeCode: String,
                          registration: String,
                          airlineId: Long,
                          id: Option[Long] = None
                         ) extends Entity[Aircraft, Long]

final case class Airline(name: String,
                         foundationDate: LocalDate,
                         countryId: Long,
                         id: Option[Long] = None
                        ) extends Entity[Airline, Long]

final case class Airport(name: String,
                         iataCode: String,
                         icaoCode: String,
                         countryId: Long,
                         id: Option[Long] = None
                        ) extends Entity[Airport, Long]