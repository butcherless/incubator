package infrastructure.persistence.postgres.repository

import doobie.Transactor
import doobie.implicits.*
import doobie.postgres.implicits.*
import domain.error.DomainError
import domain.model.{OutboxEvent, OutboxEventId}
import domain.port.out.OutboxRepository
import zio.{IO, Task, URLayer, ZLayer}
import zio.interop.catz.*

import java.util.UUID

final class DoobieOutboxRepository(xa: Transactor[Task]) extends OutboxRepository {

  override def save(event: OutboxEvent): IO[DomainError, OutboxEvent] =
    sql"""
      INSERT INTO outbox_events (id, aggregate_type, aggregate_id, event_type, payload, published)
      VALUES (${event.id.value}, ${event.aggregateType}, ${event.aggregateId}, ${event.eventType}, ${event.payload}::jsonb, ${event.published})
    """.update.run
      .transact(xa)
      .as(event)
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def findUnpublished(limit: Int): IO[DomainError, List[OutboxEvent]] =
    sql"SELECT id, aggregate_type, aggregate_id, event_type, payload::text, published FROM outbox_events WHERE NOT published ORDER BY created_at LIMIT $limit"
      .query[(UUID, String, String, String, String, Boolean)]
      .to[List]
      .transact(xa)
      .map(_.map((i, at, ai, et, p, pub) => OutboxEvent(OutboxEventId(i), at, ai, et, p, pub)))
      .mapError(e => DomainError.DatabaseError(e.getMessage))

  override def markPublished(id: OutboxEventId): IO[DomainError, Unit] =
    sql"UPDATE outbox_events SET published = TRUE WHERE id = ${id.value}"
      .update.run
      .transact(xa)
      .unit
      .mapError(e => DomainError.DatabaseError(e.getMessage))
}

object DoobieOutboxRepository {
  val layer: URLayer[Transactor[Task], OutboxRepository] =
    ZLayer.fromFunction(new DoobieOutboxRepository(_))
}
