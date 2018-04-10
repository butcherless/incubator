package com.cmartin.repository

import com.cmartin.domain.{AircraftBean, Airline, Person}
import org.springframework.data.neo4j.repository.Neo4jRepository

trait PersonRepository extends Neo4jRepository[Person, java.lang.Long]

trait AircraftRepository extends Neo4jRepository[AircraftBean, java.lang.Long]

trait AirlineRepository extends Neo4jRepository[Airline, java.lang.Long]

