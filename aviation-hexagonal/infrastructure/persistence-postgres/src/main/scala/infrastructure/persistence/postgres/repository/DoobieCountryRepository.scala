package infrastructure.persistence.postgres.repository

import doobie.Transactor
import doobie.implicits.*
import domain.error.DomainError
import domain.model.{Country, CountryCode}
import domain.port.out.CountryRepository
import shared.Pagination
import zio.*
import zio.interop.catz.*

final class DoobieCountryRepository(xa: Transactor[Task]) extends CountryRepository {

  override def findByCode(code: CountryCode): IO[DomainError, Option[Country]] =
    sql"SELECT code, name FROM countries WHERE code = ${code.value}"
      .query[(String, String)]
      .option
      .transact(xa)
      .map(_.map((c, n) => Country(CountryCode(c), n)))
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def findAll(pagination: Pagination): IO[DomainError, List[Country]] =
    sql"SELECT code, name FROM countries ORDER BY code LIMIT ${pagination.pageSize} OFFSET ${pagination.offset}"
      .query[(String, String)]
      .to[List]
      .transact(xa)
      .map(_.map((c, n) => Country(CountryCode(c), n)))
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def save(country: Country): IO[DomainError, Country] =
    sql"""
      INSERT INTO countries (code, name)
      VALUES (${country.code.value}, ${country.name})
      ON CONFLICT (code) DO UPDATE SET name = EXCLUDED.name
    """.update.run
      .transact(xa)
      .as(country)
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def delete(code: CountryCode): IO[DomainError, Unit] =
    sql"DELETE FROM countries WHERE code = ${code.value}"
      .update.run
      .transact(xa)
      .unit
      .mapError(e => DomainError.DatabaseError(e.getMessage))
}

object DoobieCountryRepository {
  val layer: URLayer[Transactor[Task], CountryRepository] =
    ZLayer.fromFunction(new DoobieCountryRepository(_))
}
