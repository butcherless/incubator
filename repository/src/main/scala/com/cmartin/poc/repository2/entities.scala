package com.cmartin.poc.repository2

import java.time.{LocalDate, LocalDateTime, LocalTime}

/**
  * Database entity based on a type and an identifier
  *
  * @tparam T type for the entity
  * @tparam I identifier for the entity
  */
trait Entity[T, I] {
  val id: Option[I]
}


/*
    C O U T R Y
 */
final case class Country(name: String,
                         code: String,
                         id: Option[Long] = None
                        ) extends Entity[Country, Long]

/*
    A I R P O R T
 */
final case class Airport(name: String,
                         iataCode: String,
                         icaoCode: String,
                         countryId: Long,
                         id: Option[Long] = None
                        ) extends Entity[Airport, Long]

/*
    A I R L I N E
 */
final case class Airline(name: String,
                         foundationDate: LocalDate,
                         countryId: Long,
                         id: Option[Long] = None
                        ) extends Entity[Airline, Long]

/*
    A I R C R A F T
 */
final case class Aircraft(typeCode: String,
                          registration: String,
                          airlineId: Long,
                          id: Option[Long] = None
                         ) extends Entity[Aircraft, Long]


/*
    R O U T E
 */
final case class Route(distance: Double,
                       originId: Long,
                       destinationId: Long,
                       id: Option[Long] = None
                      ) extends Entity[Route, Long]


/*
    F L I G H T
 */
final case class Flight(code: String,
                        alias: String,
                        schedDeparture: LocalTime,
                        schedArrival: LocalTime,
                        airlineId: Long,
                        routeId: Long,
                        id: Option[Long] = None
                       ) extends Entity[Flight, Long]

/*
     J O U R N E Y
  */
final case class Journey(departureDate: LocalTime,
                         arrivalDate: LocalTime,
                         flightId: Long,
                         aircraftId: Long,
                         id: Option[Long] = None
                        ) extends Entity[Journey, Long]


/*
    P O S I T I O N
 */

final case class Point(longitude: Float, latitude: Float)

final case class Coordinates(point: Point, altitude: Float)

final case class Position(coordinates: Coordinates, dateTime: LocalDateTime)