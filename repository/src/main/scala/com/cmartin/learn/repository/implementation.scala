package com.cmartin.learn.repository

import com.cmartin.learn.repository.definition.BaseRepository
import com.cmartin.learn.repository.tables._
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

package object implementation {

  class AircraftRepository(implicit db: Database) extends BaseRepository[Aircraft, Fleet](db) {

    lazy val entities = TableQuery[Fleet]

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

  class AirlineRepository(implicit db: Database) extends BaseRepository[Airline, Airlines](db) {
    lazy val entities = TableQuery[Airlines]

    def findByCountryCode(code: String): Future[Seq[Airline]] = {
      val query = for {
        airline <- entities
        country <- airline.country if country.code === code
      } yield airline

      query.result
    }
  }

  class AirportRepository(implicit db: Database) extends BaseRepository[Airport, Airports](db) {
    lazy val entities = TableQuery[Airports]

    def findByCountryCode(code: String): Future[Seq[Airport]] = {
      val query = for {
        airport <- entities
        country <- airport.country if country.code === code
      } yield airport

      query.result
    }
  }

  class CountryRepository(implicit db: Database) extends BaseRepository[Country, Countries](db) {
    lazy val entities = TableQuery[Countries]

    def findByCode(code: String): Future[Option[Country]] = entities.filter(_.code === code).result.headOption
  }

  class FlightRepository(implicit db: Database) extends BaseRepository[Flight, Flights](db) {
    lazy val entities = TableQuery[Flights]

    def findByCode(code: String): Future[Option[Flight]] = entities.filter(_.code === code).result.headOption

    def findByOrigin(origin: String): Future[Seq[Flight]] = {
      val query = for {
        flight <- entities
        route <- flight.route
        airport <- route.origin if airport.iataCode === origin
      } yield flight

      query.result
    }
  }

  class JourneyRepository(implicit db: Database) extends BaseRepository[Journey, Journeys](db) {
    lazy val entities = TableQuery[Journeys]
  }

  class RouteRepository(implicit db: Database) extends BaseRepository[Route, Routes](db) {
    lazy val entities = TableQuery[Routes]

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

}