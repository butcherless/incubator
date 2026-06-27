package infrastructure.messaging.kafka.serde

import zio.kafka.serde.Serde

case class RouteCreatedEvent(
    routeId: String,
    originIata: String,
    destinationIata: String,
    airlineIcao: String,
    distanceKm: Int
)

object RouteEventCodec {
  // TODO: implement with Circe + ZIO Kafka 3.x Serde API
  val routeCreatedSerde: Serde[Any, RouteCreatedEvent] = ???
}
