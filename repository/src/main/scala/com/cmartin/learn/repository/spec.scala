package com.cmartin.learn.repository

import slick.jdbc.H2Profile.api._
import slick.lifted.{TableQuery, Tag}

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

  trait BaseRepository[E <: LongBaseEntity, T <: BaseTable[E]] {
    val entities: TableQuery[T]

    def findById(id: Long) = entities.filter(_.id === id)

    def count() = entities.length

    def entityReturningId() = entities returning entities.map(_.id)
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
