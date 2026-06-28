package infrastructure.messaging.kafka.producer

import domain.error.DomainError
import domain.model.OutboxEvent
import domain.port.out.EventPublisher
import infrastructure.messaging.kafka.config.KafkaConfig
import zio.{IO, ZIO, ZLayer, URLayer, TaskLayer}
import zio.kafka.producer.{Producer, ProducerSettings}

final class RouteEventProducer(producer: Producer) extends EventPublisher {

  override def publish(event: OutboxEvent): IO[DomainError, Unit] =
    // TODO: implement per event type using ZIO Kafka 3.x API
    ZIO.logInfo(s"Publishing event: ${event.eventType} for ${event.aggregateId}").unit
}

object RouteEventProducer {

  val layer: URLayer[Producer, EventPublisher] =
    ZLayer.fromFunction(new RouteEventProducer(_))

  val producerLayer: TaskLayer[Producer] =
    ZLayer.scoped {
      Producer.make(
        ProducerSettings(List(KafkaConfig.default.bootstrapServers))
      )
    }
}
