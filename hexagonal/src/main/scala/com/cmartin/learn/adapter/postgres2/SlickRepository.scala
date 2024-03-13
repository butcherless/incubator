package com.cmartin.learn.adapter.postgres2

import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcActionComponent, JdbcProfile}
import slick.lifted.{ForeignKeyQuery, Index, PrimaryKey, ProvenShape}

import java.time.LocalDate
import scala.concurrent.Future

object SlickRepository {

  object Model {

    type LongTuple = (Long, Long)

    trait IdentifiedDbo[T] {
      val id: T
    }

    trait LongDbo extends IdentifiedDbo[Option[Long]]

    trait RelationDbo extends IdentifiedDbo[LongTuple]

    final case class CountryDbo(
        name: String,
        code: String,
        id: Option[Long] = None
    ) extends LongDbo

    final case class AirportDbo(
        name: String,
        iataCode: String,
        icaoCode: String,
        countryId: Long,
        id: Option[Long] = None
    ) extends LongDbo

    final case class AircraftJourneyRelDbo(
        date: LocalDate,
        id: LongTuple
    ) extends RelationDbo
  }

  trait Profile {
    val profile: JdbcProfile
  }

  object TableNames {
    val airlines        = "AIRLINES"
    val airports        = "AIRPORTS"
    val countries       = "COUNTRIES"
    val fleet           = "FLEET"
    val flights         = "FLIGHTS"
    val journeys        = "JOURNEYS"
    val routes          = "ROUTES"
    val aircraftJourney = "AIRCRAFT_JOURNEY"
  }

  trait BaseDefinitions {
    self: JdbcProfile =>

    import Model._
    import api._

    abstract class LongBasedTable[T <: LongDbo](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
      /* primary key column */
      def id: Rep[Long] =
        column[Long]("ID", O.PrimaryKey, O.AutoInc)
    }

    abstract class RelationBasedTable[T <: RelationDbo](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
      /* primary key columns */
      def left: Rep[Long]  = column[Long]("LEFT")
      def right: Rep[Long] = column[Long]("RIGHT")
      def pk: PrimaryKey   = primaryKey("pk_aircraft_journey", (left, right))
    }

    abstract class AbstractRelationRepository[E <: RelationDbo, T <: RelationBasedTable[E]] {
      val entities: TableQuery[T]

      private def pkFilter(t: T, id: LongTuple): Rep[Boolean] =
        t.left === id._1 && t.right === id._2

      def findById(id: LongTuple): DBIO[Option[E]] =
        entities.filter(t => pkFilter(t, id)).result.headOption

      def findAll(): DBIO[Seq[E]] =
        entities.result

      def count(): DBIO[Int] =
        entities.length.result

      def insert(e: E): DBIO[LongTuple] =
        entityReturningId() += e

      def insert(seq: Seq[E]): DBIO[Seq[LongTuple]] =
        entities returning entities.map(e => (e.left, e.right)) ++= seq

      def update(e: E): DBIO[Int] =
        entities.filter(t => pkFilter(t, e.id)).update(e)

      def delete(id: LongTuple): DBIO[Int] =
        entities.filter(t => pkFilter(t, id)).delete

      def deleteAll(): DBIO[Int] =
        entities.delete

      private def entityReturningId(): ReturningInsertActionComposer[E, LongTuple] =
        entities returning entities.map(e => (e.left, e.right))
    }

    abstract class AbstractLongRepository[E <: LongDbo, T <: LongBasedTable[E]] {
      val entities: TableQuery[T]

      def findById(id: Option[Long]): DBIO[Option[E]] = {
        entities.filter(_.id === id).result.headOption
      }

      def findAll(): DBIO[Seq[E]] =
        entities.result

      def count(): DBIO[Int] =
        entities.length.result

      def insert(e: E): DBIO[Long] =
        entityReturningId() += e

      def insert(seq: Seq[E]): DBIO[Seq[Long]] =
        entities returning entities.map(_.id) ++= seq

      def update(e: E): DBIO[Int] =
        entities.filter(_.id === e.id).update(e)

      def delete(id: Long): DBIO[Int] =
        entities.filter(_.id === id).delete

      def deleteAll(): DBIO[Int] =
        entities.delete

      private def entityReturningId(): ReturningInsertActionComposer[E, Long] =
        entities returning entities.map(_.id)
    }

  }

  trait AviationRepositories extends BaseDefinitions {
    self: JdbcProfile =>
    import Model._
    import api._

    lazy val countries        = TableQuery[CountryTable]
    lazy val airports         = TableQuery[AirportTable]
    lazy val aircraftJourneys = TableQuery[AircraftJourneyTable]

    /* C O U N T R Y
     */
    final class CountryTable(tag: Tag) extends LongBasedTable[CountryDbo](tag, TableNames.countries) {
      // property columns:
      def name: Rep[String] = column[String]("NAME")

      def code: Rep[String] = column[String]("CODE")

      def * : ProvenShape[CountryDbo] =
        (name, code, id.?).<>(CountryDbo.tupled, CountryDbo.unapply)

      // indexes
      def codeIndex: Index =
        index("code_idx", code, unique = true)
    }

    class CountryRepository extends AbstractLongRepository[CountryDbo, CountryTable] {
      override val entities: TableQuery[CountryTable] = countries
    }

    /* A I R P O R T S
     */
    final class AirportTable(tag: Tag) extends LongBasedTable[AirportDbo](tag, TableNames.airports) {
      // property columns:
      def name: Rep[String] = column[String]("NAME")

      def iataCode: Rep[String] = column[String]("IATA_CODE")

      def icaoCode: Rep[String] = column[String]("ICAO_CODE")

      // foreign columns:
      def countryId: Rep[Long] = column[Long]("COUNTRY_ID")

      def * : ProvenShape[AirportDbo] =
        (name, iataCode, icaoCode, countryId, id.?).<>(AirportDbo.tupled, AirportDbo.unapply)

      // foreign keys
      def country: ForeignKeyQuery[CountryTable, CountryDbo] =
        foreignKey("FK_COUNTRY_AIRPORT", countryId, countries)(_.id)

      // indexes
      def iataIndex: Index =
        index("iataCode_index", iataCode, unique = true)
    }

    class AirportRepository extends AbstractLongRepository[AirportDbo, AirportTable] {
      override val entities: TableQuery[AirportTable] = airports
    }

    /* A I R C R A F T -- J O U R N E Y   R E L A T I O N
     */
    final class AircraftJourneyTable(tag: Tag)
        extends RelationBasedTable[AircraftJourneyRelDbo](tag, TableNames.aircraftJourney) {
      // property columns:
      def date: Rep[LocalDate] = column[LocalDate]("DATE")

      def * : ProvenShape[AircraftJourneyRelDbo] =
        (date, (left, right)).<>(AircraftJourneyRelDbo.tupled, AircraftJourneyRelDbo.unapply)
    }

    class AircraftJourneyRepository extends AbstractRelationRepository[AircraftJourneyRelDbo, AircraftJourneyTable] {
      override val entities: TableQuery[AircraftJourneyTable] = aircraftJourneys

      def findByAircraft(id: Long): DBIO[Seq[AircraftJourneyRelDbo]] = {
        val query = for {
          aj <- entities if aj.right === id
        } yield aj

        query.result
      }

      def findByAircraftAndDate(id: Long, date: LocalDate): DBIO[Seq[AircraftJourneyRelDbo]] = {
        val query = for {
          aj <- entities if aj.right === id && aj.date === date
        } yield aj

        query.result
      }

    }
  }

  trait RepositoryBundle
      extends JdbcProfile
      with JdbcActionComponent.MultipleRowsPerStatementSupport
      with AviationRepositories {

    val countriesRepo       = new CountryRepository
    val airportsRepo        = new AirportRepository
    val aircraftJourneyRepo = new AircraftJourneyRepository
  }

  class RepositoryLayer(configPath: String) extends RepositoryBundle {
    val config = DatabaseConfig.forConfig[JdbcProfile](configPath)

    implicit def executeFromDb[A](action: api.DBIO[A]): Future[A] =
      config.db.run(action)
  }
}
