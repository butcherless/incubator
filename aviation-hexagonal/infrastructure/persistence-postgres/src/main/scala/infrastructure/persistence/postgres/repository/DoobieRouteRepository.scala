package infrastructure.persistence.postgres.repository

import doobie.Transactor
import doobie.implicits.*
import doobie.postgres.implicits.*
import domain.error.DomainError
import domain.model.{IataCode, IcaoCode, Route, RouteId}
import domain.port.out.RouteRepository
import shared.Pagination
import zio.{IO, Task, URLayer, ZLayer}
import zio.interop.catz.*

import java.util.UUID

final class DoobieRouteRepository(xa: Transactor[Task]) extends RouteRepository {

  override def findById(id: RouteId): IO[DomainError, Option[Route]] =
    sql"SELECT id, origin_iata, destination_iata, airline_icao, distance_km FROM routes WHERE id = ${id.value}"
      .query[(UUID, String, String, String, Int)]
      .option
      .transact(xa)
      .map(_.map((i, o, d, a, dist) => Route(RouteId(i), IataCode(o), IataCode(d), IcaoCode(a), dist)))
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def findAll(pagination: Pagination): IO[DomainError, List[Route]] =
    sql"SELECT id, origin_iata, destination_iata, airline_icao, distance_km FROM routes ORDER BY id LIMIT ${pagination.pageSize} OFFSET ${pagination.offset}"
      .query[(UUID, String, String, String, Int)]
      .to[List]
      .transact(xa)
      .map(_.map((i, o, d, a, dist) => Route(RouteId(i), IataCode(o), IataCode(d), IcaoCode(a), dist)))
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def save(route: Route): IO[DomainError, Route] =
    sql"""
      INSERT INTO routes (id, origin_iata, destination_iata, airline_icao, distance_km)
      VALUES (${route.id.value}, ${route.origin.value}, ${route.destination.value}, ${route.airlineIcao.value}, ${route.distanceKm})
      ON CONFLICT (id) DO UPDATE
        SET distance_km = EXCLUDED.distance_km
    """.update.run
      .transact(xa)
      .as(route)
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def delete(id: RouteId): IO[DomainError, Unit] =
    sql"DELETE FROM routes WHERE id = ${id.value}"
      .update.run
      .transact(xa)
      .unit
      .mapError(e => DomainError.DatabaseError(e.getMessage))
}

object DoobieRouteRepository {
  val layer: URLayer[Transactor[Task], RouteRepository] =
    ZLayer.fromFunction(new DoobieRouteRepository(_))
}
