package com.cmartin.repository

import com.cmartin.domain._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.repository.Neo4jRepository

trait PersonRepository extends Neo4jRepository[Person, java.lang.Long]

trait AircraftRepository extends Neo4jRepository[AircraftBean, java.lang.Long]

trait AirlineRepository extends Neo4jRepository[Airline, java.lang.Long]

trait RouteRepository extends Neo4jRepository[Route, java.lang.Long]

trait FlightRepository extends Neo4jRepository[Flight, java.lang.Long]


class Repos {
  @Autowired var acRepo: AircraftRepository = _
  @Autowired var alRepo: AirlineRepository = _
  @Autowired var flRepo: FlightRepository = _
  @Autowired var peRepo: PersonRepository = _
  @Autowired var roRepo: RouteRepository = _
}

