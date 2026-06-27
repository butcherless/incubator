package domain.model

import java.util.UUID

opaque type OutboxEventId = UUID

object OutboxEventId {
  def apply(value: UUID): OutboxEventId        = value
  def generate: OutboxEventId                  = UUID.randomUUID()
  extension (o: OutboxEventId) def value: UUID = o
}

case class OutboxEvent(
    id: OutboxEventId,
    aggregateType: String,
    aggregateId: String,
    eventType: String,
    payload: String,
    published: Boolean
)
