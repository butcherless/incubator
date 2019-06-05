package com.cmartin.learn.repository

import slick.jdbc
import slick.jdbc.H2Profile.api._
import slick.lifted.{ TableQuery, Tag }
import slick.sql.SqlAction

import scala.concurrent.Future

package object definition {

  /**
   * Database entity based on a type and an identifier
   *
   * @tparam T type for the entity
   * @tparam I identifier for the entity
   */
  trait Entity[T, I] {
    val id: Option[I]
  }

  abstract class BaseTable[T <: Entity[T, Long]](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  }

  abstract class BaseRepository[E <: Entity[E, Long], T <: BaseTable[E]](db: Database) {
    val entities: TableQuery[T]

    protected implicit def executeFromDb[A](action: SqlAction[A, NoStream, _ <: slick.dbio.Effect]): Future[A] = db.run(action)

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
    def entityReturningId(): jdbc.H2Profile.ReturningInsertActionComposer[E, Long] = entities returning entities.map(_.id)

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

}
