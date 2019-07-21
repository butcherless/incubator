package com.cmartin.poc.repository2

import java.time.{LocalDate, LocalTime}

import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.Future

trait Repositories extends RelationalInfrastructure {
  self: Profile =>

  import profile.api._

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
    val AIRBUS_330_200 = "A332"
    val AIRBUS_350_900 = "A359"
    val BOEING_737_800 = "B738"
    val BOEING_787_800 = "B788"
  }

  /*
    C O U N T R Y
 */

  final class Countries(tag: Tag) extends RelationalTable[Country](tag, TableNames.countries) {

    // property columns:
    def name = column[String]("NAME")

    def code = column[String]("CODE")

    def * = (name, code, id.?) <> (Country.tupled, Country.unapply)

    // indexes
    def codeIndex = index("code_idx", code, unique = true)
  }

  lazy val countries = TableQuery[Countries]

  /*
    A I R L I N E
 */

  final class Airlines(tag: Tag) extends RelationalTable[Airline](tag, TableNames.airlines) {

    // property columns:
    def name = column[String]("NAME")

    def foundationDate = column[LocalDate]("FOUNDATION_DATE")

    // foreign columns:
    def countryId = column[Long]("COUNTRY_ID")

    def * = (name, foundationDate, countryId, id.?) <> (Airline.tupled, Airline.unapply)

    // foreign keys
    def country = foreignKey("FK_COUNTRY_AIRLINE", countryId, countries)(_.id)
  }

  lazy val airlines = TableQuery[Airlines]


  final class Fleet(tag: Tag) extends RelationalTable[Aircraft](tag, TableNames.fleet) {

    // property columns:
    def typeCode = column[String]("TYPE_CODE")

    def registration = column[String]("REGISTRATION")

    def airlineId = column[Long]("AIRLINE_ID")

    def * = (typeCode, registration, airlineId, id.?) <> (Aircraft.tupled, Aircraft.unapply)

    // foreign keys
    def airline = foreignKey("FK_AIRLINE_FLEET", airlineId, airlines)(_.id)
  }

  lazy val fleet = TableQuery[Fleet]


  final class Airports(tag: Tag) extends RelationalTable[Airport](tag, TableNames.airports) {

    // property columns:
    def name = column[String]("NAME")

    def iataCode = column[String]("IATA_CODE")

    def icaoCode = column[String]("ICAO_CODE")

    // foreign columns:
    def countryId = column[Long]("COUNTRY_ID")

    def * = (name, iataCode, icaoCode, countryId, id.?) <> (Airport.tupled, Airport.unapply)

    // foreign keys
    def country = foreignKey("FK_COUNTRY_AIRPORT", countryId, countries)(_.id)

    // indexes
    def iataIndex = index("iataCode_index", iataCode, unique = true)
  }

  lazy val airports = TableQuery[Airports]


  final class Routes(tag: Tag) extends RelationalTable[Route](tag, TableNames.routes) {

    // property columns:
    def distance = column[Double]("DISTANCE")

    // foreign key columns:
    def originId = column[Long]("ORIGIN_ID")

    def destinationId = column[Long]("DESTINATION_ID")

    def * = (distance, originId, destinationId, id.?) <> (Route.tupled, Route.unapply)

    // foreign keys
    def origin =
      foreignKey("FK_ORIGIN", originId, airports)(
        origin => origin.id,
        onDelete = ForeignKeyAction.Cascade
      )

    def destination =
      foreignKey("FK_DESTINATION", destinationId, airports)(
        destination => destination.id,
        onDelete = ForeignKeyAction.Cascade
      )

    // indexes, compound
    def originDestinationIndex =
      index("origin_destination_index", (originId, destinationId), unique = true)
  }

  lazy val routes = TableQuery[Routes]


  final class Flights(tag: Tag) extends RelationalTable[Flight](tag, TableNames.flights) {
    // property columns:
    def code = column[String]("CODE")

    def alias = column[String]("ALIAS")

    def schedDeparture = column[LocalTime]("SCHEDULED_DEPARTURE")

    def schedArrival = column[LocalTime]("SCHEDULED_ARRIVAL")

    // foreign columns:
    def airlineId = column[Long]("AIRLINE_ID")

    def routeId = column[Long]("ROUTE_ID")

    def * =
      (code, alias, schedDeparture, schedArrival, airlineId, routeId, id.?) <> (Flight.tupled, Flight.unapply)

    // foreign keys
    def route = foreignKey("FK_ROUTE", routeId, routes)(_.id)

    def airline = foreignKey("FK_AIRLINE_FLIGHT", airlineId, airlines)(_.id)

    // indexes
    def codeIndex = index("code_index", code, unique = true)

  }

  lazy val flights = TableQuery[Flights]


  /*
      R E P O S I T O R I E S
   */

  class AirlineRepository(val db: JdbcBackend#DatabaseDef) extends RelationalRepository[Airline, Airlines](db) {
    override lazy val entities = airlines

    def findByCountryCode(code: String): Future[Seq[Airline]] = {
      val query = for {
        airline <- entities
        country <- airline.country if country.code === code
      } yield airline

      query.result
    }
  }

  class CountryRepository(val db: JdbcBackend#DatabaseDef) extends RelationalRepository[Country, Countries](db) {
    override lazy val entities = countries

    def findByCode(code: String): Future[Option[Country]] =
      entities.filter(_.code === code).result.headOption
  }

  class AircraftRepository(val db: JdbcBackend#DatabaseDef) extends RelationalRepository[Aircraft, Fleet](db) {
    override lazy val entities = fleet

    def findByRegistration(registration: String): Future[Option[Aircraft]] =
      entities.filter(_.registration === registration).result.headOption

    def findByAirlineName(name: String): Future[Seq[Aircraft]] = {

      val query = for {
        aircraft <- entities
        airline <- aircraft.airline if airline.name === name
      } yield aircraft

      query.result
    }
  }

  class AirportRepository(val db: JdbcBackend#DatabaseDef) extends RelationalRepository[Airport, Airports](db) {
    lazy val entities = airports

    def findByCountryCode(code: String): Future[Seq[Airport]] = {
      val query = for {
        airport <- entities
        country <- airport.country if country.code === code
      } yield airport

      query.result
    }
  }

  final class RouteRepository(val db: JdbcBackend#DatabaseDef) extends RelationalRepository[Route, Routes](db) {
    override lazy val entities = routes

    def findByIataDestination(iataCode: String): Future[Seq[Route]] = {
      val query = for {
        route <- entities
        airport <- route.destination if airport.iataCode === iataCode
      } yield route

      query.result
    }

    def findByIataOrigin(iataCode: String): Future[Seq[Route]] = {
      val query = for {
        route <- entities
        airport <- route.origin if airport.iataCode === iataCode
      } yield route

      query.result
    }
  }

  final class FlightRepository(val db: JdbcBackend#DatabaseDef) extends RelationalRepository[Flight, Flights](db) {
    override lazy val entities = flights

    def findByCode(code: String): Future[Option[Flight]] =
      entities.filter(_.code === code).result.headOption

    def findByOrigin(origin: String): Future[Seq[Flight]] = {
      val query = for {
        flight <- entities
        route <- flight.route
        airport <- route.origin if airport.iataCode === origin
      } yield flight

      query.result
    }

  }

}

class DatabaseAccessLayer(val profile: JdbcProfile) extends Profile with Repositories

class DatabaseAccessLayer2(val config: DatabaseConfig[JdbcProfile]) extends Profile with Repositories {
  override val profile = config.profile
}