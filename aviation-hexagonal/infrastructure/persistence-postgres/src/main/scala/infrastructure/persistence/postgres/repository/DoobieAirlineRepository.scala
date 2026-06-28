package infrastructure.persistence.postgres.repository

import doobie.Transactor
import doobie.implicits.*
import domain.error.DomainError
import domain.model.{Airline, CountryCode, IcaoCode}
import domain.port.out.AirlineRepository
import shared.Pagination
import zio.{IO, Task, ZLayer, URLayer}
import zio.interop.catz.*

final class DoobieAirlineRepository(xa: Transactor[Task]) extends AirlineRepository {

  override def findByIcao(icao: IcaoCode): IO[DomainError, Option[Airline]] =
    sql"SELECT icao_code, name, country_code FROM airlines WHERE icao_code = ${icao.value}"
      .query[(String, String, String)]
      .option
      .transact(xa)
      .map(_.map((i, n, cc) => Airline(IcaoCode(i), n, CountryCode(cc))))
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def findAll(pagination: Pagination): IO[DomainError, List[Airline]] =
    sql"SELECT icao_code, name, country_code FROM airlines ORDER BY icao_code LIMIT ${pagination.pageSize} OFFSET ${pagination.offset}"
      .query[(String, String, String)]
      .to[List]
      .transact(xa)
      .map(_.map((i, n, cc) => Airline(IcaoCode(i), n, CountryCode(cc))))
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def save(airline: Airline): IO[DomainError, Airline] =
    sql"""
      INSERT INTO airlines (icao_code, name, country_code)
      VALUES (${airline.icao.value}, ${airline.name}, ${airline.countryCode.value})
      ON CONFLICT (icao_code) DO UPDATE
        SET name = EXCLUDED.name
    """.update.run
      .transact(xa)
      .as(airline)
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def delete(icao: IcaoCode): IO[DomainError, Unit] =
    sql"DELETE FROM airlines WHERE icao_code = ${icao.value}"
      .update.run
      .transact(xa)
      .unit
      .mapError(e => DomainError.DatabaseError(e.getMessage))
}

object DoobieAirlineRepository {
  val layer: URLayer[Transactor[Task], AirlineRepository] =
    ZLayer.fromFunction(new DoobieAirlineRepository(_))
}
