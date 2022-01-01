package com.cmartin.learn.repository

import slick.jdbc.JdbcProfile

/** Abstract profile for accessing databases. */
trait Profile {
  val profile: JdbcProfile
}

trait RelationalInfrastructure {
  self: Profile =>

  import profile.api._

  abstract class RelationalTable[T <: Entity[T, Long]](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  }

  trait RelationalRepository[F[_], E] {

    /** Retrieve all the entities in the repository
      *
      * @return
      *   the entity sequence
      */
    def findAll(): F[Seq[E]]

    /** Retrieve the entity option by its id
      *
      * @param id
      *   identifier for the entity to found
      * @return
      *   Some(e) or None
      */
    def findById(id: Long): F[Option[E]]

    /** Retrieve the repository entity count
      *
      * @return
      *   number of entities in the repo
      */
    def count(): F[Int]

    /** Inserts the entity returning the generated identifier
      *
      * @param e
      *   entity to be added
      * @return
      *   entity id after the insert
      */
    def insert(e: E): F[Long]

    /** Inserts a sequence of entities returning the generated sequence of identifiers
      *
      * @param seq
      *   entity sequence
      * @return
      *   generated identifier sequence after the insert
      */
    def insert(seq: Seq[E]): F[Seq[Long]]

    /** Updates the entity in the repository
      *
      * @param e
      *   entity to be updated
      * @return
      *   number of entities affected
      */
    def update(e: E): F[Int]

    /** Deletes the entity with the identifier supplied
      *
      * @param id
      *   entity identifier
      * @return
      *   number of entites affected
      */
    def delete(id: Long): F[Int]

    /** Deletes all the entities from the repository
      *
      * @return
      *   number of entites affected
      */
    def deleteAll(): F[Int]
  }

  trait AirlineRelationalRepository[F[_]] extends RelationalRepository[F, Airline] {

    /** Retrieve the airline sequence that belong to a country
      *
      * @param code
      *   country code
      * @return
      *   thje sequence
      */
    def findByCountryCode(code: String): F[Seq[Airline]]
  }

  trait CountryRelationalRepository[F[_]] extends RelationalRepository[F, Country] {

    /** Retrieve a country option by its code
      *
      * @param code
      *   country code
      * @return
      *   the country option
      */
    def findByCode(code: String): F[Option[Country]]
  }

  trait AircraftRelationalRepository[F[_]] extends RelationalRepository[F, Aircraft] {
    def findByRegistration(registration: String): F[Option[Aircraft]]

    def findByAirlineName(name: String): F[Seq[Aircraft]]
  }

  trait AirportRelationalRepository[F[_]] extends RelationalRepository[F, Airport] {
    def findByCountryCode(code: String): F[Seq[Airport]]
  }

  trait RouteRelationalRepository[F[_]] extends RelationalRepository[F, Route] {
    def findByIataDestination(iataCode: String): F[Seq[Route]]

    def findByIataOrigin(iataCode: String): F[Seq[Route]]
  }

  trait FlightRelationalRepository[F[_]] extends RelationalRepository[F, Flight] {
    def findByCode(code: String): F[Option[Flight]]

    def findByOrigin(origin: String): F[Seq[Flight]]
  }

  trait JourneyRelationalRepository[F[_]] extends RelationalRepository[F, Journey] {
    // TODO implement operations
  }

  abstract class AbstractRelationalRepository[E <: Entity[E, Long], T <: RelationalTable[E]]
      extends RelationalRepository[DBIO, E] {
    val entities: TableQuery[T]

    override def findAll(): DBIO[Seq[E]] = entities.result

    override def findById(id: Long): DBIO[Option[E]] =
      entities.filter(_.id === id).result.headOption

    override def count(): DBIO[Int] = entities.length.result

    override def insert(e: E): DBIO[Long] = entityReturningId() += e

    override def insert(seq: Seq[E]): DBIO[Seq[Long]] =
      entities returning entities.map(_.id) ++= seq

    override def update(e: E): DBIO[Int] = entities.filter(_.id === e.id).update(e)

    override def delete(id: Long): DBIO[Int] = entities.filter(_.id === id).delete

    override def deleteAll(): DBIO[Int] = entities.delete

    private def entityReturningId(): profile.ReturningInsertActionComposer[E, Long] =
      entities returning entities.map(_.id)
  }

}
