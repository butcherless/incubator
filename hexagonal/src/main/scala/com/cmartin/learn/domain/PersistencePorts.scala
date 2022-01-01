package com.cmartin.learn.domain

import scala.concurrent.Future

object PersistencePorts {
  import Model._

  /** Domain Port
    *
    * @tparam F
    * @tparam E
    */
  trait BaseRepository[F[_], E] {
    def insert(entity: E): F[E]
    def update(entity: E): F[E]
    def delete(v: E): F[E]
  }

  /* Domain Port
     TODO refactor: Future => zio.Task
   */
  trait CountryRepository extends BaseRepository[Future, Country] {
    def findByCode(code: String): Future[Country]
  }

  /* Domain Port
     TODO refactor: Future => zio.Task
   */
  trait AirportRepository extends BaseRepository[Future, Airport] {
    def findByCountryCode(code: String): Future[Seq[Airport]]
  }

}
