package infrastructure.persistence.postgres.repository

import doobie.Transactor
import doobie.implicits.*
import domain.error.DomainError
import domain.model.{Airport, CountryCode, IataCode}
import domain.port.out.AirportRepository
import shared.Pagination
import zio.{IO, Task, URLayer, ZLayer}
import zio.interop.catz.*

final class DoobieAirportRepository(xa: Transactor[Task]) extends AirportRepository {

  override def findByIata(iata: IataCode): IO[DomainError, Option[Airport]] =
    sql"SELECT iata_code, name, city, country_code FROM airports WHERE iata_code = ${iata.value}"
      .query[(String, String, String, String)]
      .option
      .transact(xa)
      .map(_.map((i, n, c, cc) => Airport(IataCode(i), n, c, CountryCode(cc))))
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def findAll(pagination: Pagination): IO[DomainError, List[Airport]] =
    sql"SELECT iata_code, name, city, country_code FROM airports ORDER BY iata_code LIMIT ${pagination.pageSize} OFFSET ${pagination.offset}"
      .query[(String, String, String, String)]
      .to[List]
      .transact(xa)
      .map(_.map((i, n, c, cc) => Airport(IataCode(i), n, c, CountryCode(cc))))
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def save(airport: Airport): IO[DomainError, Airport] =
    sql"""
      INSERT INTO airports (iata_code, name, city, country_code)
      VALUES (${airport.iata.value}, ${airport.name}, ${airport.city}, ${airport.countryCode.value})
      ON CONFLICT (iata_code) DO UPDATE
        SET name = EXCLUDED.name, city = EXCLUDED.city
    """.update.run
      .transact(xa)
      .as(airport)
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def delete(iata: IataCode): IO[DomainError, Unit] =
    sql"DELETE FROM airports WHERE iata_code = ${iata.value}"
      .update.run
      .transact(xa)
      .unit
      .mapError(e => DomainError.DatabaseError(e.getMessage))
}

object DoobieAirportRepository {
  val layer: URLayer[Transactor[Task], AirportRepository] =
    ZLayer.fromFunction(new DoobieAirportRepository(_))
}
