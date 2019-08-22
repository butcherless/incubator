package com.cmartin.learn.repository

import slick.jdbc.JdbcProfile

trait Profile {
  val profile: JdbcProfile
}

trait RelationalInfrastructure {
  self: Profile =>

  import profile.api._

  abstract class RelationalTable[T <: Entity[T, Long]](tag: Tag, tableName: String)
      extends Table[T](tag, tableName) {
    // primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  }

  abstract class RelationalRepository[E <: Entity[E, Long], T <: RelationalTable[E]] {
    val entities: TableQuery[T]

    def findAll(): DBIO[Seq[E]] = entities.result

    /**
      * Retrieve the entity option
      *
      * @param id identifier for the entity to found
      * @return Some(e) or None
      */
    def findById(id: Long): DBIO[Option[E]] = entities.filter(_.id === id).result.headOption

    /**
      * Retrieve the repository entity count
      *
      * @return number of entities in the repo
      */
    def count(): DBIO[Int] = entities.length.result

    /**
      * Helper for insert operations
      *
      * @return id for the entity added
      */
    def entityReturningId(): profile.ReturningInsertActionComposer[E, Long] =
      entities returning entities.map(_.id)

    /**
      * Inserts the entity returning the generated identifier
      *
      * @param e entity to be added
      * @return entity id after the insert
      */
    def insert(e: E): DBIO[Long] = entityReturningId += e

    /**
      * Inserts a sequence of entities returning the generated sequence of identifiers
      *
      * @param seq entity sequence
      * @return generated identifier sequence after the insert
      */
    def insert(seq: Seq[E]): DBIO[Seq[Long]] = entities returning entities.map(_.id) ++= seq

    /**
      * Updates the entity in the repository
      *
      * @param e entity to be updated
      * @return number of entities updated
      */
    def update(e: E): DBIO[Int] = entities.filter(_.id === e.id).update(e)

    /**
      * Deletes the entity with the identifier supplied
      *
      * @param id entity identifier
      * @return number of entites affected
      */
    def delete(id: Long): DBIO[Int] = entities.filter(_.id === id).delete

    /**
      * Deletes all the entities from the repository
      *
      * @return number of entites affected
      */
    def deleteAll(): DBIO[Int] = entities.delete
  }

}
