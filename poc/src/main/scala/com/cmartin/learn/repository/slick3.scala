package com.cmartin.learn.repository

import java.sql.Date
import java.time.{LocalDate, LocalTime}

import slick.jdbc
import slick.jdbc.H2Profile.api._
import slick.lifted.{TableQuery, Tag}
import slick.sql.SqlAction

import scala.concurrent.Future

package object slick3 {

  object TableNames {
    val airlines  = "AIRLINES"
    val countries = "COUNTRIES"
  }

  trait Entity[T, I] {
    val id: Option[I]
  }

  abstract class BaseTable[T <: Entity[T, Long]](tag: Tag, tableName: String)
      extends Table[T](tag, tableName) {
    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  }

  abstract class BaseRepository[E <: Entity[E, Long], T <: BaseTable[E]](db: Database) {
    val entities: TableQuery[T]

    protected implicit def executeFromDb[A](
        action: SqlAction[A, NoStream, _ <: slick.dbio.Effect]
    ): Future[A] = db.run(action)

    def findAll(): Future[Seq[E]] = entities.result

    /**
      * Retrieve the entity option
      *
      * @param id identifier for the entity to found
      * @return Some(e) or None
      */
    def findById(id: Long): Future[Option[E]] = entities.filter(_.id === id).result.headOption

    /**
      * Retrieve the repository entity count
      *
      * @return number of entities in the repo
      */
    def count(): Future[Int] = entities.length.result

    /**
      * Helper for insert operations
      *
      * @return id for the entity added
      */
    def entityReturningId(): jdbc.H2Profile.ReturningInsertActionComposer[E, Long] =
      entities returning entities.map(_.id)

    /**
      * Inserts the entity returning the generated identifier
      *
      * @param e entity to be added
      * @return entity id after the insert
      */
    def insert(e: E): Future[Long] = entityReturningId += e

    /**
      * Inserts a sequence of entities returning the generated sequence of identifiers
      *
      * @param seq entity sequence
      * @return generated identifier sequence after the insert
      */
    def insert(seq: Seq[E]): Future[Seq[Long]] = entities returning entities.map(_.id) ++= seq

    /**
      * Updates the entity in the repository
      *
      * @param e entity to be updated
      * @return number of entities updated
      */
    def update(e: E): Future[Int] = entities.filter(_.id === e.id).update(e)

    /**
      * Deletes the entity with the identifier supplied
      *
      * @param id entity identifier
      * @return number of entites affected
      */
    def delete(id: Long): Future[Int] = entities.filter(_.id === id).delete
  }

  /*
    maps the custom types of the application to the database
   */
  object CustomColumnTypes {

    implicit val localDateType =
      MappedColumnType.base[LocalDate, Date](ld => Date.valueOf(ld), dt => dt.toLocalDate)

    implicit val localTimeType =
      MappedColumnType.base[LocalTime, String](lt => lt.toString, st => LocalTime.parse(st))

  }

  /*
      C O U N T R Y
   */
  final case class Country(name: String, code: String, id: Option[Long] = None)
      extends Entity[Country, Long]

  final class Countries(tag: Tag) extends BaseTable[Country](tag, TableNames.countries) {

    // property columns:
    def name = column[String]("NAME")

    def code = column[String]("CODE")

    def * = (name, code, id.?) <> (Country.tupled, Country.unapply)

    // indexes
    def codeIndex = index("code_idx", code, unique = true)
  }

  class CountryRepository(implicit db: Database) extends BaseRepository[Country, Countries](db) {
    lazy val entities = TableQuery[Countries]

    def findByCode(code: String): Future[Option[Country]] =
      entities.filter(_.code === code).result.headOption
  }

  /*
      A I R L I N E
   */
  final case class Airline(
      name: String,
      foundationDate: LocalDate,
      countryId: Long,
      id: Option[Long] = None
  ) extends Entity[Airline, Long]

  final class Airlines(tag: Tag) extends BaseTable[Airline](tag, TableNames.airlines) {

    import CustomColumnTypes.localDateType

    // property columns:
    def name = column[String]("NAME")

    def foundationDate = column[LocalDate]("FOUNDATION_DATE")

    // foreign columns:
    def countryId = column[Long]("COUNTRY_ID")

    def * = (name, foundationDate, countryId, id.?) <> (Airline.tupled, Airline.unapply)

    // foreign keys
    def country = foreignKey("FK_COUNTRY_AIRLINE", countryId, TableQuery[Countries])(_.id)
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

}
