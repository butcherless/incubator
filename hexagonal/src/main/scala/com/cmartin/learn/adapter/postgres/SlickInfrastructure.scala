package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.adapter.postgres.Model.{AirportDbo, CountryDbo, LongEntity}
import slick.jdbc.JdbcProfile

object SlickInfrastructure {
  trait Profile {
    val profile: JdbcProfile
  }

  trait SlickRepository {
    self: Profile =>

    import profile.api._

    case class RepositoryException(m: String) extends RuntimeException(m)

    abstract class LongBasedTable[T <: LongEntity](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
      /* primary key column */
      def id: Rep[Long] =
        column[Long]("ID", O.PrimaryKey, O.AutoInc)
    }

    trait AbstractRepository[F[_], E] {

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

      /** Inserts a sequence of entities returning the generated sequence of
        * identifiers
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

    abstract class AbstractSlickRepository[E <: LongEntity, T <: LongBasedTable[E]]
        extends AbstractRepository[DBIO, E] {

      val entities: TableQuery[T]

      override def findAll(): DBIO[Seq[E]] =
        entities.result

      override def findById(id: Long): DBIO[Option[E]] =
        entities.filter(_.id === id).result.headOption

      override def count(): DBIO[Int] =
        entities.length.result

      override def insert(e: E): DBIO[Long] =
        entityReturningId() += e

      override def insert(seq: Seq[E]): DBIO[Seq[Long]] =
        entities returning entities.map(_.id) ++= seq

      override def update(e: E): DBIO[Int] =
        entities.filter(_.id === e.id).update(e)

      override def delete(id: Long): DBIO[Int] =
        entities.filter(_.id === id).delete

      override def deleteAll(): DBIO[Int] =
        entities.delete

      private def entityReturningId(): profile.ReturningInsertActionComposer[E, Long] =
        entities returning entities.map(_.id)
    }

    trait AbstractCountrySlickRepository[F[_]] extends AbstractRepository[F, CountryDbo] {
      def findByCode(code: String): F[Option[CountryDbo]]
    }

    trait AbstractAirportSlickRepository[F[_]] extends AbstractRepository[F, AirportDbo] {
      def findByIataCode(code: String): F[Option[AirportDbo]]
      def findByCountryCode(code: String): F[Seq[AirportDbo]]
    }
  }
}
