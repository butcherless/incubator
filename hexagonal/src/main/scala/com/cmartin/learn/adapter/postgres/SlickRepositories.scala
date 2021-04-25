package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.adapter.postgres.SlickInfrastructure.Profile
import com.cmartin.learn.adapter.postgres.SlickInfrastructure.SlickRepository
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait SlickRepositories extends SlickRepository {
  self: Profile =>

  import Model._
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

  def checkElement[T](option: Option[T], error: String): DBIO[T] = {
    option
      .fold[DBIO[T]](
        DBIO.failed(RepositoryException(error))
      )(e => DBIO.successful(e))

  }
  def checkHeadElement[T](seq: Seq[T], error: String): DBIO[T] = {
    checkElement(seq.headOption, error)
  }

  /*
  C O U N T R Y
   */
  final class Countries(tag: Tag) extends LongBasedTable[CountryDbo](tag, TableNames.countries) {
    // property columns:
    def name: Rep[String] = column[String]("NAME")

    def code: Rep[String] = column[String]("CODE")

    def * : ProvenShape[CountryDbo] = (name, code, id.?).<>(CountryDbo.tupled, CountryDbo.unapply)

    // indexes
    def codeIndex = index("code_idx", code, unique = true)
  }

  lazy val countries = TableQuery[Countries]

  class CountrySlickRepository
      extends AbstractSlickRepository[CountryDbo, Countries]
      with AbstractCountrySlickRepository[DBIO] {

    override val entities = countries

    override def findByCode(code: String): DBIO[Option[CountryDbo]] = {
      entities.filter(_.code === code).result.headOption
    }
  }

  /*
  A I R P O R T S
   */

  final class Airports(tag: Tag) extends LongBasedTable[AirportDbo](tag, TableNames.airports) {
    // property columns:
    def name: Rep[String] = column[String]("NAME")

    def iataCode: Rep[String] = column[String]("IATA_CODE")

    def icaoCode: Rep[String] = column[String]("ICAO_CODE")

    // foreign columns:
    def countryId: Rep[Long] = column[Long]("COUNTRY_ID")

    def * : ProvenShape[AirportDbo] =
      (name, iataCode, icaoCode, countryId, id.?).<>(AirportDbo.tupled, AirportDbo.unapply)

    // foreign keys
    def country = foreignKey("FK_COUNTRY_AIRPORT", countryId, countries)(_.id)

    // indexes
    def iataIndex = index("iataCode_index", iataCode, unique = true)
  }

  lazy val airports = TableQuery[Airports]

  class AirportSlickRepository
      extends AbstractSlickRepository[AirportDbo, Airports]
      with AbstractAirportSlickRepository[DBIO] {

    override val entities = airports

    override def findByIataCode(code: String): DBIO[Option[AirportDbo]] = {
      entities.filter(_.iataCode === code).result.headOption
    }

    override def findByCountryCode(code: String): DBIO[Seq[AirportDbo]] = {
      val query = for {
        airport <- entities
        country <- airport.country if country.code === code
      } yield airport

      query.result
    }
  }
}

object SlickRepositories {

  class DatabaseLayer(val config: DatabaseConfig[JdbcProfile])
      extends Profile
      with SlickRepositories {
    import profile.api._

    override val profile = config.profile

    implicit def executeFromDb[A](dbAction: DBIO[A]): Future[A] =
      config.db.run(dbAction)
  }

  trait DAL1 extends SlickRepositories { self: Profile =>
    import profile.api._

    implicit val ec: ExecutionContext
    val config: DatabaseConfig[JdbcProfile]

    implicit def executeFromDb[A](dbAction: DBIO[A]): Future[A] =
      config.db.run(dbAction)

    val countryRepo = new CountrySlickRepository
    val airportRepo = new AirportSlickRepository
  }

  trait DAL extends Profile with SlickRepositories {
    import profile.api._

    //val config: DatabaseConfig[JdbcProfile]
    val countryRepo = new CountrySlickRepository
    val airportRepo = new AirportSlickRepository

    implicit val ec: ExecutionContext

    implicit def executeFromDb[A](dbAction: DBIO[A]): Future[A]
  }

  class Database2Layer(configPath: String) extends DAL {

    //TODO make config private, create database layer for testing
    val config: DatabaseConfig[JdbcProfile] =
      DatabaseConfig.forConfig[JdbcProfile](configPath)

    override val profile = config.profile

    override implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

    override implicit def executeFromDb[A](dbAction: profile.api.DBIO[A]): Future[A] =
      config.db.run(dbAction)
  }

}
