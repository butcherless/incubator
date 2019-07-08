package com.cmartin.poc.repository2

import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.Future

trait Repositories
  extends DatabaseTables {
  self: Profile =>

  import profile.api._


  class AirlineRepository(val db: JdbcBackend#DatabaseDef) extends BaseRepository[Airline, Airlines](db) {
    lazy val entities = TableQuery[Airlines]

    def findByCountryCode(code: String): Future[Seq[Airline]] = {
      val query = for {
        airline <- entities
        country <- airline.country if country.code === code
      } yield airline

      query.result
    }
  }

  class CountryRepository(val db: JdbcBackend#DatabaseDef) extends BaseRepository[Country, Countries](db) {
    lazy val entities = TableQuery[Countries]

    def findByCode(code: String): Future[Option[Country]] =
      entities.filter(_.code === code).result.headOption
  }

  class AircraftRepository(val db: JdbcBackend#DatabaseDef) extends BaseRepository[Aircraft, Fleet](db) {

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


}

class DatabaseAccessLayer(val profile: JdbcProfile) extends Profile with Repositories

class DatabaseAccessLayer2(val config: DatabaseConfig[JdbcProfile]) extends Profile with Repositories {
  val profile = config.profile
}