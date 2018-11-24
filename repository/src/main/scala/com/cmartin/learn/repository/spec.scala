package com.cmartin.learn.repository

import slick.jdbc
import slick.jdbc.H2Profile.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.Future

package object spec {

  /**
    * Database entity
    *
    * @tparam K identifier or key
    */
  trait BaseEntity[K] {
    val id: K
  }

  /**
    * Entity based on a Long Identifier
    */
  abstract class LongBaseEntity extends BaseEntity[Option[Long]]

  abstract class BaseRepository[E <: LongBaseEntity, T <: BaseTable[E]](db: Database) {
    val entities: TableQuery[T]

    /**
      * Retrieve the entity option
      *
      * @param id identifier for the entity to found
      * @return Some(e) or None
      */
    def findById(id: Long): Future[Option[E]] = db.run(entities.filter(_.id === id).result.headOption)

    /**
      * Retrieve the repository entity count
      *
      * @return number of entities in the repo
      */
    def count(): Future[Int] = db.run(entities.length.result)

    /**
      * Helper for insert operations
      *
      * @return id for the entity added
      */
    def entityReturningId(): jdbc.H2Profile.ReturningInsertActionComposer[E, Long] = entities returning entities.map(_.id)

    /**
      * Inserts the entity returning the generated identifier
      *
      * @param e entity to be added
      * @return entity id after the insert
      */
    def insert(e: E): Future[Long] = db.run(entityReturningId += e)

    /**
      * Inserts a sequence of entities returning the generated sequence of identifiers
      *
      * @param seq entity sequence
      * @return generated identifier sequence after the insert
      */
    def insert(seq: Seq[E]): Future[Seq[Long]] = db.run(entities returning entities.map(_.id) ++= seq)

    /**
      * Updates the entity in the repository
      *
      * @param e entity to be updated
      * @return number of entities updated
      */
    def update(e: E) = db.run(entities.filter(_.id === e.id).update(e))

    /**
      * Deletes the entity with the identifier supplied
      *
      * @param id entity identifier
      * @return number of entites affected
      */
    def delete(id: Long) = db.run(entities.filter(_.id === id).delete)
  }

  abstract class BaseTable[E <: LongBaseEntity](tag: Tag, tableName: String) extends Table[E](tag, tableName) {
    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  }


  /*
      POC
   */

  trait SimpleRepository[M[_], T, K] {

    def findAll(filter: (T) => Boolean): M[List[T]]

    def findById(k: K): M[T]

    def remove(t: T): M[K]

    def removeAll(filter: (T) => Boolean): M[List[K]]

    def save(t: T): M[K]

    def count(): M[Long]
  }

}
