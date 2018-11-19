package com.cmartin.learn.repository

import java.time.{LocalDate, LocalTime}

import com.cmartin.learn.repository.frm._
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery


package object implementation {

  trait BaseEntity[K] {
    val id: K
  }

  trait BaseRepository[E <: LongBaseEntity, T <: BaseTable[E]] {
    val entities: TableQuery[T]

    def findById(id: Long) = entities.filter(_.id === id)

    def count() = entities.length

    def entityReturningId() = entities returning entities.map(_.id)
  }

  abstract class LongBaseEntity extends BaseEntity[Option[Long]]


  object AircraftRepository extends BaseRepository[Aircraft, Fleet] {
    lazy val entities = TableQuery[Fleet]

    def findByRegistration(registration: String) = entities.filter(_.registration === registration)

    def insertAction(typeCode: String, registration: String, airlineId: Long) =
      entityReturningId += Aircraft(typeCode, registration, airlineId)
  }

  object AirlineRepository extends BaseRepository[Airline, Airlines] {
    lazy val entities = TableQuery[Airlines]

    def insertAction(name: String, foundationDate: LocalDate) =
      entityReturningId += Airline(name, foundationDate)
  }

  object AirportRepository extends BaseRepository[Airport, Airports] {
    lazy val entities = TableQuery[Airports]

    def insertAction(name: String, iataCode: String, icaoCode: String, countryId: Long) =
      entityReturningId += Airport(name, iataCode, icaoCode, countryId)

    def findByCountryCode(code: String) = {
      val query = for {
        airport <- entities
        country <- airport.country if country.code === code
      } yield airport

      query
    }
  }

  object CountryRepository extends BaseRepository[Country, Countries] {
    lazy val entities = TableQuery[Countries]

    def findByCodeQuery(code: String) = {
      entities.filter(_.code === code) //.result.headOption
    }

    def insertAction(name: String, code: String) =
      entityReturningId += Country(name, code)
  }

  object FlightRepository extends BaseRepository[Flight, Flights] {
    lazy val entities = TableQuery[Flights]

    def insertAction(code: String, alias: String, departure: LocalTime, arrival: LocalTime, airlineId: Long, routeId: Long) =
      entityReturningId += Flight(code, alias, departure, arrival, airlineId, routeId)

    def findByCode(code: String) = entities.filter(_.code === code).result

    def findByOrigin(origin: String) = {
      val query = for {
        flight <- entities
        route <- flight.route
        airport <- route.origin if airport.iataCode === origin
      } yield flight

      query.result
    }

  }

  object JourneyRepository extends BaseRepository[Journey, Journeys] {
    lazy val entities = TableQuery[Journeys]

    def insertAction(departureDate: LocalTime, arrivalDate: LocalTime, flightId: Long, aircraftId: Long) =
      entityReturningId += Journey(departureDate, arrivalDate, flightId, aircraftId)
  }


  object RouteRepository extends BaseRepository[Route, Routes] {
    lazy val entities = TableQuery[Routes]

    def insertAction(distance: Double, originId: Long, destinationId: Long) =
      entityReturningId += Route(distance, originId, destinationId)

    def findDestinationsByOrigin(iataCode: String) = {
      val query = for {
        route <- entities
        airport <- route.destination
        origin <- route.origin if origin.iataCode === iataCode
      } yield airport

      query.result
    }

  }

}