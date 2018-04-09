package com.cmartin.repository

import com.cmartin.domain.Person
import org.springframework.data.neo4j.repository.Neo4jRepository

trait PersonRepository extends Neo4jRepository[Person, java.lang.Long]
