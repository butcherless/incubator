package com.cmartin.poc

import java.time.LocalDate

import com.cmartin.learn.repository.tables.TableNames
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.Future

package object repository2 {

  /*
    https://books.underscore.io/essential-slick/essential-slick-3.html#Modelling
    https://github.com/slick/slick/tree/master/samples
 */

  object TypeCodes {
    val AIRBUS_320     = "A320"
    val AIRBUS_330_200 = "A332"
    val AIRBUS_350_900 = "A359"
    val BOEING_737_800 = "B738"
    val BOEING_787_800 = "B788"
  }


  /**
    * Database entity based on a type and an identifier
    *
    * @tparam T type for the entity
    * @tparam I identifier for the entity
    */
  trait Entity[T, I] {
    val id: Option[I]
  }


  final case class Country(name: String,
                           code: String,
                           id: Option[Long] = None
                          ) extends Entity[Country, Long]

  final case class Airline(name: String,
                           foundationDate: LocalDate,
                           countryId: Long,
                           id: Option[Long] = None
                          ) extends Entity[Airline, Long]

  final case class Aircraft(typeCode: String,
                            registration: String,
                            airlineId: Long,
                            id: Option[Long] = None
                           ) extends Entity[Aircraft, Long]


  trait Profile {
    val profile: JdbcProfile
  }

  trait Definitions {
    self: Profile =>

    import profile.api._

    abstract class BaseTable[T <: Entity[T, Long]](tag: Tag, tableName: String)
      extends Table[T](tag, tableName) {
      // primary key column:
      def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    }

    abstract class BaseRepository[E <: Entity[E, Long], T <: BaseTable[E]](db: JdbcBackend#DatabaseDef) {
      val entities: TableQuery[T]

      implicit def executeFromDb[A](action: DBIO[A]): Future[A] = db.run(action)

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
      def entityReturningId(): profile.ReturningInsertActionComposer[E, Long] = entities returning entities.map(_.id)

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

      /**
        * Creates the entity relational schema
        *
        * @return
        */
      def create: Future[Unit] = entities.schema.createIfNotExists

      /**
        * Creates the entity relational schema*
        *
        * @return
        */
      def drop: Future[Unit] = entities.schema.dropIfExists
    }

  }

  trait DatabaseTables extends Definitions {
    self: Profile =>

    import profile.api._

    /*
      C O U N T R Y
   */

    final class Countries(tag: Tag) extends BaseTable[Country](tag, TableNames.countries) {

      // property columns:
      def name = column[String]("NAME")

      def code = column[String]("CODE")

      def * = (name, code, id.?) <> (Country.tupled, Country.unapply)

      // indexes
      def codeIndex = index("code_idx", code, unique = true)
    }

    /*
      A I R L I N E
   */

    final class Airlines(tag: Tag) extends BaseTable[Airline](tag, TableNames.airlines) {

      // property columns:
      def name = column[String]("NAME")

      def foundationDate = column[LocalDate]("FOUNDATION_DATE")

      // foreign columns:
      def countryId = column[Long]("COUNTRY_ID")

      def * = (name, foundationDate, countryId, id.?) <> (Airline.tupled, Airline.unapply)

      // foreign keys
      def country = foreignKey("FK_COUNTRY_AIRLINE", countryId, TableQuery[Countries])(_.id)
    }

    final class Fleet(tag: Tag) extends BaseTable[Aircraft](tag, TableNames.fleet) {

      // property columns:
      def typeCode = column[String]("TYPE_CODE")

      def registration = column[String]("REGISTRATION")

      def airlineId = column[Long]("AIRLINE_ID")

      def * = (typeCode, registration, airlineId, id.?) <> (Aircraft.tupled, Aircraft.unapply)

      // foreign keys
      def airline = foreignKey("FK_AIRLINE", airlineId, TableQuery[Airlines])(_.id)

    }

  }

}
