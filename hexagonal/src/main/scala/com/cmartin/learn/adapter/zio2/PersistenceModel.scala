package com.cmartin.learn.adapter.zio2

object PersistenceModel {

  /** Identifier for a database object
    */
  trait DboId[T] {
    val id: T
  }

  // type aliases
  type LongDbo = DboId[Long]

  final case class CountryDbo(
      name: String,
      code: String,
      id: Long = 0L
  ) extends LongDbo
}
