package com.cmartin.learn.repository

import java.sql.Date
import java.time.{LocalDate, LocalTime}

import slick.jdbc.H2Profile.api._

package object frm {

  object TableNames {
    val airlines = "AIRLINES"
    val airports = "AIRPORTS"
    val countries = "COUNTRIES"
    val fleet = "FLEET"
    val flights = "FLIGHTS"
    val journeys = "JOURNEYS"
    val routes = "ROUTES"
  }

  object TypeCodes {
    val AIRBUS_320 = "A320"
    val AIRBUS_350_900 = "A359"
    val BOEING_737_800 = "B738"
    val BOEING_787_800 = "B788"
  }

  /*
    maps the custom types of the application to the database
 */
  object CustomColumnTypes {

    implicit val localDateType =
      MappedColumnType.base[LocalDate, Date](
        ld => Date.valueOf(ld),
        dt => dt.toLocalDate
      )

    implicit val localTimeType =
      MappedColumnType.base[LocalTime, String](
        lt => lt.toString,
        st => LocalTime.parse(st)
      )

  }


  /*
      A I R C R A F T
   */
  final case class Aircraft(typeCode: String, registration: String, airlineId: Long, id: Option[Long] = None)

  final class Fleet(tag: Tag) extends Table[Aircraft](tag, TableNames.fleet) {
    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    // property columns:
    def typeCode = column[String]("TYPE_CODE")

    def registration = column[String]("REGISTRATION")

    def airlineId = column[Long]("AIRLINE_ID")

    def * = (typeCode, registration, airlineId, id.?) <> (Aircraft.tupled, Aircraft.unapply)

    // foreign keys
    def airline = foreignKey("FK_AIRLINE", airlineId, TableQuery[Airlines])(_.id)

  }

  /*
      A I R L I N E
   */
  final case class Airline(name: String, foundationDate: LocalDate, id: Option[Long] = None)

  final class Airlines(tag: Tag) extends Table[Airline](tag, TableNames.airlines) {

    import CustomColumnTypes.localDateType

    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    // property columns:
    def name = column[String]("NAME")

    def foundationDate = column[LocalDate]("FOUNDATION_DATE")

    def * = (name, foundationDate, id.?) <> (Airline.tupled, Airline.unapply)
  }

  lazy val airlines = TableQuery[Airlines]


  /*
      C O U N T R Y
   */
  final case class Country(name: String, code: String, id: Option[Long] = None)

  final class Countries(tag: Tag) extends Table[Country](tag, TableNames.countries) {
    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    // property columns:
    def name = column[String]("NAME")

    def code = column[String]("CODE")

    def * = (name, code, id.?) <> (Country.tupled, Country.unapply)

    // indexes
    def codeIndex = index("code_idx", code, unique = true)
  }


  /*
      A I R P O R T
   */
  final case class Airport(name: String, iataCode: String, icaoCode: String, countryId: Long, id: Option[Long] = None)

  final class Airports(tag: Tag) extends Table[Airport](tag, TableNames.airports) {
    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    // property columns:
    def name = column[String]("NAME")

    def iataCode = column[String]("IATA_CODE")

    def icaoCode = column[String]("ICAO_CODE")

    // foreign columns:
    def countryId = column[Long]("COUNTRY_ID")

    def * = (name, iataCode, icaoCode, countryId, id.?) <> (Airport.tupled, Airport.unapply)

    // foreign keys
    def country = foreignKey("FK_COUNTRY", countryId, TableQuery[Countries])(_.id)

    // indexes
    def iataIndex = index("iataCode_index", iataCode, unique = true)
  }

  lazy val airports = TableQuery[Airports]


  /*
      R O U T E
   */
  final case class Route(distance: Double, originId: Long, destinationId: Long, id: Option[Long] = None)

  final class Routes(tag: Tag) extends Table[Route](tag, TableNames.routes) {
    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    // property columns:
    def distance = column[Double]("DISTANCE")

    // foreign key columns:
    def originId = column[Long]("ORIGIN_ID")

    def destinationId = column[Long]("DESTINATION_ID")

    def * = (distance, originId, destinationId, id.?) <> (Route.tupled, Route.unapply)

    // foreign keys
    def origin = foreignKey("FK_ORIGIN", originId, TableQuery[Airports])(origin =>
      origin.id, onDelete = ForeignKeyAction.Cascade)

    def destination = foreignKey("FK_DESTINATION", destinationId, TableQuery[Airports])(destination =>
      destination.id, onDelete = ForeignKeyAction.Cascade)

    // indexes, compound
    def originDestinationIndex = index("origin_destination_index", (originId, destinationId), unique = true)
  }

  lazy val routes = TableQuery[Routes]


  /*
      F L I G H T
  */
  final case class Flight(code: String, alias: String, schedDeparture: LocalTime, schedArrival: LocalTime, routeId: Long, id: Option[Long] = None)

  final class Flights(tag: Tag) extends Table[Flight](tag, TableNames.flights) {

    import CustomColumnTypes.localTimeType

    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    // property columns:
    def code = column[String]("CODE")

    def alias = column[String]("ALIAS")

    //TODO Custom Column Mappings, LocalTime
    def schedDeparture = column[LocalTime]("SCHEDULED_DEPARTURE")

    //TODO Custom Column Mappings, LocalTime
    def schedArrival = column[LocalTime]("SCHEDULED_ARRIVAL")

    // foreign columns:
    def routeId = column[Long]("ROUTE_ID")

    def * = (code, alias, schedDeparture, schedArrival, routeId, id.?) <> (Flight.tupled, Flight.unapply)

    // foreign keys
    def route = foreignKey("FK_ROUTE", routeId, TableQuery[Routes])(_.id)

    // indexes
    def codeIndex = index("code_index", code, unique = true)

  }

  lazy val flights = TableQuery[Flights]


  /*
      J O U R N E Y
   */
  final case class Journey(departureDate: LocalTime, arrivalDate: LocalTime, flightId: Long, aircraftId: Long, id: Option[Long] = None)

  final class Journeys(tag: Tag) extends Table[Journey](tag, TableNames.journeys) {

    import CustomColumnTypes.localTimeType

    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    // property columns:
    def departureDate = column[LocalTime]("DEPARTURE_DATE")

    def arrivalDate = column[LocalTime]("ARRIVAL_DATE")

    // foreign columns:
    def flightId = column[Long]("FLIGHT_ID")

    def aircraftId = column[Long]("AIRCRAFT_ID")

    def * = (departureDate, arrivalDate, flightId, aircraftId, id.?) <> (Journey.tupled, Journey.unapply)

    // foreign keys
    def flight = foreignKey("FK_FLIGHT", flightId, TableQuery[Flights])(_.id)

    def aircraft = foreignKey("FK_AIRCRAFT", aircraftId, TableQuery[Fleet])(_.id)
  }

  lazy val journeys = TableQuery[Journeys]

}
