package com.cmartin.learn.adapter.zio2

object PersistenceModel {

  /** Identifier for a database persistent object
    */
  trait DboId[T] {
    val id: T
  }

  /** type alias for a Long based persistent object
    */
  type LongDbo = DboId[Long]

  /** Database Object thar Stores a Country in the database.
    *
    * Searches by business identifier are performed using an index. The
    * persistence identifier is synthetic.
    *
    * It's used to relate this object to others.
    *
    * @param name
    *   country name
    * @param code
    *   country code -business identifier-
    * @param id
    *   persistence identifier
    */
  final case class CountryDbo(
      name: String,
      code: String,
      id: Long = 0L
  ) extends LongDbo
}
