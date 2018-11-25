package com.cmartin.learn.repository

import com.cmartin.learn.repository.frm._
import com.cmartin.learn.repository.spec.BaseRepository
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery


package object implementation {

  class AircraftRepository(implicit db: Database) extends BaseRepository[Aircraft, Fleet](db) {
    lazy val entities = TableQuery[Fleet]

    def findByRegistration(registration: String) =
      db.run(entities.filter(_.registration === registration).result.headOption)
  }

  class AirlineRepository(implicit db: Database) extends BaseRepository[Airline, Airlines](db) {
    lazy val entities = TableQuery[Airlines]
  }


  class AirportRepository(implicit db: Database) extends BaseRepository[Airport, Airports](db) {
    lazy val entities = TableQuery[Airports]

    def findByCountryCode(code: String) = {
      val query = for {
        airport <- entities
        country <- airport.country if country.code === code
      } yield airport

      db.run(query.result)
    }
  }

  class CountryRepository(implicit db: Database) extends BaseRepository[Country, Countries](db) {
    lazy val entities = TableQuery[Countries]

    def findByCode(code: String) =
      db.run(entities.filter(_.code === code).result.headOption)
  }

  class FlightRepository(implicit db: Database) extends BaseRepository[Flight, Flights](db) {
    lazy val entities = TableQuery[Flights]

    def findByCode(code: String) =
      db.run(entities.filter(_.code === code).result.headOption)

    def findByOrigin(origin: String) = {
      val query = for {
        flight <- entities
        route <- flight.route
        airport <- route.origin if airport.iataCode === origin
      } yield flight

      db.run(query.result)
    }

  }

  class JourneyRepository(implicit db: Database) extends BaseRepository[Journey, Journeys](db) {
    lazy val entities = TableQuery[Journeys]
  }

  class RouteRepository(implicit db: Database) extends BaseRepository[Route, Routes](db) {
    lazy val entities = TableQuery[Routes]

    def findDestinationsByOrigin(iataCode: String) = {
      val query = for {
        route <- entities
        airport <- route.destination
        origin <- route.origin if origin.iataCode === iataCode
      } yield airport

      db.run(query.result)
    }

  }

}