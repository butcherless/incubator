package infrastructure.messaging.kafka.relay

import domain.port.out.{EventPublisher, OutboxRepository}
import zio.{ZIO, Schedule, Duration}

object OutboxRelay {

  // Polls unpublished outbox events and forwards them to Kafka, then marks them as published.
  val relay: ZIO[OutboxRepository & EventPublisher, Nothing, Nothing] =
    ZIO
      .serviceWithZIO[OutboxRepository](_.findUnpublished(limit = 50))
      .flatMap { events =>
        ZIO.foreachDiscard(events) { event =>
          ZIO
            .serviceWithZIO[EventPublisher](_.publish(event))
            .flatMap(_ => ZIO.serviceWithZIO[OutboxRepository](_.markPublished(event.id)))
            .catchAll(err => ZIO.logError(s"Relay failed for event ${event.id}: $err"))
        }
      }
      .catchAll(err => ZIO.logError(s"Relay query failed: $err"))
      .repeat(Schedule.fixed(Duration.fromSeconds(5L)))
      .unit
      .forever
}
