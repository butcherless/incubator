package com.cmartin.learn.repository

import java.time.{LocalDate, LocalTime}

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait Repositories extends RelationalInfrastructure {
  self: Profile =>

  import profile.api._

  object TableNames {
    val airlines  = "AIRLINES"
    val airports  = "AIRPORTS"
    val countries = "COUNTRIES"
    val fleet     = "FLEET"
    val flights   = "FLIGHTS"
    val journeys  = "JOURNEYS"
    val routes    = "ROUTES"
  }

  object TypeCodes {
    val AIRBUS_320     = "A320"
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

  /*
    F L E E T
   */

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

  /*
    A I R P O R T S
   */

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

  /*
    R O U T E S
   */

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

  /*
    F L I G T S
   */

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
    J O U R N E Y S
   */

  final class Journeys(tag: Tag) extends RelationalTable[Journey](tag, TableNames.journeys) {
    // property columns:
    def departureDate = column[LocalTime]("DEPARTURE_DATE")

    def arrivalDate = column[LocalTime]("ARRIVAL_DATE")

    // foreign columns:
    def flightId = column[Long]("FLIGHT_ID")

    def aircraftId = column[Long]("AIRCRAFT_ID")

    def * =
      (departureDate, arrivalDate, flightId, aircraftId, id.?) <> (Journey.tupled, Journey.unapply)

    // foreign keys
    def flight = foreignKey("FK_FLIGHT_JOURNEY", flightId, flights)(_.id)

    def aircraft = foreignKey("FK_AIRCRAFT_JOURNEY", aircraftId, fleet)(_.id)
  }

  lazy val journeys = TableQuery[Journeys]

/*
case class UserRole(var role: String, var extra: String)
case class UserInfo(var login: String, var password: String, var firstName: String, var lastName: String)

case class User(id: Option[String], var info: UserInfo, var role: UserRole)

class UserTable(tag: Tag) extends Table[User](tag, "USER") {

  def id = column[String]("id", O.PrimaryKey)
  def role = column[String]("role", O.NotNull)
  def extra = column[String]("extra", O.NotNull)
  def login = column[String]("login", O.NotNull)
  def password = column[String]("password", O.NotNull)
  def firstName = column[String]("first_name", O.NotNull)
  def lastName = column[String]("last_name", O.NotNull)

  /** Projection */
  def * = (
    id,
    (login, password, firstName, lastName),
    (role, extra)
  ).shaped <> (

  { case (id, userInfo, userRole) =>
    User(Option[id], UserInfo.tupled.apply(userInfo), UserRole.tupled.apply(userRole))
  },
  { u: User =>
      def f1(p: UserInfo) = UserInfo.unapply(p).get
      def f2(p: UserRole) = UserRole.unapply(p).get
      Some((u.id.get, f1(u.info), f2(u.role)))
  })
}
 */

  final class Assets(tag: Tag) extends RelationalTable[Asset](tag, "ASSETS") {
    def tenantId = column[Long]("TENANT_ID")

    def assetId = column[Long]("ASSET_ID")

    def predicates = column[String]("PREDICATES")

    def * = (
      (tenantId, assetId),
      predicates,
      id
      ).shaped <> ( {
      case (businessId, pred_, id_) =>
        Asset(AssetSeedId.tupled.apply(businessId), pred_, Option(id_))
    }, { a: Asset =>
      def f1(p: AssetSeedId) = AssetSeedId.unapply(p).get
      Some((f1(a.businessId), a.predicates, a.id.get))
    }
    )
  }

  /*
      R E P O S I T O R I E S
   */

  class AirlineRepository
      extends AbstractRelationalRepository[Airline, Airlines]
      with AirlineRelationalRepository[DBIO, Airline] {
    override lazy val entities = airlines

    override def findByCountryCode(code: String): DBIO[Seq[Airline]] = {
      val query = for {
        airline <- entities
        country <- airline.country if country.code === code
      } yield airline

      query.result
    }
  }

  class CountryRepository
      extends AbstractRelationalRepository[Country, Countries]
      with CountryRelationalRepository[DBIO, Country] {
    override lazy val entities = countries

    override def findByCode(code: String): DBIO[Option[Country]] =
      entities.filter(_.code === code).result.headOption
  }

  class AircraftRepository
      extends AbstractRelationalRepository[Aircraft, Fleet]
      with AircraftRelationalRepository[DBIO, Aircraft] {
    override lazy val entities = fleet

    override def findByRegistration(registration: String): DBIO[Option[Aircraft]] =
      entities.filter(_.registration === registration).result.headOption

    override def findByAirlineName(name: String): DBIO[Seq[Aircraft]] = {
      val query = for {
        aircraft <- entities
        airline  <- aircraft.airline if airline.name === name
      } yield aircraft

      query.result
    }
  }

  class AirportRepository
      extends AbstractRelationalRepository[Airport, Airports]
      with AirportRelationalRepository[DBIO, Airport] {
    lazy val entities = airports

    override def findByCountryCode(code: String): DBIO[Seq[Airport]] = {
      val query = for {
        airport <- entities
        country <- airport.country if country.code === code
      } yield airport

      query.result
    }
  }

  final class RouteRepository
      extends AbstractRelationalRepository[Route, Routes]
      with RouteRelationalRepository[DBIO, Route] {
    override lazy val entities = routes

    override def findByIataDestination(iataCode: String): DBIO[Seq[Route]] = {
      val query = for {
        route   <- entities
        airport <- route.destination if airport.iataCode === iataCode
      } yield route

      query.result
    }

    override def findByIataOrigin(iataCode: String): DBIO[Seq[Route]] = {
      val query = for {
        route   <- entities
        airport <- route.origin if airport.iataCode === iataCode
      } yield route

      query.result
    }
  }

  final class FlightRepository
      extends AbstractRelationalRepository[Flight, Flights]
      with FlightRelationalRepository[DBIO, Flight] {
    override lazy val entities = flights

    override def findByCode(code: String): DBIO[Option[Flight]] =
      entities.filter(_.code === code).result.headOption

    override def findByOrigin(origin: String): DBIO[Seq[Flight]] = {
      val query = for {
        flight  <- entities
        route   <- flight.route
        airport <- route.origin if airport.iataCode === origin
      } yield flight

      query.result
    }
  }

  final class JourneyRepository
      extends AbstractRelationalRepository[Journey, Journeys]
      with JourneyRelationalRepository[DBIO, Journey] {
    override lazy val entities = journeys
  }
}

class DatabaseAccessLayer(val profile: JdbcProfile) extends Profile with Repositories

class DatabaseAccessLayer2(val config: DatabaseConfig[JdbcProfile])
    extends Profile
    with Repositories {
  override val profile = config.profile
}
