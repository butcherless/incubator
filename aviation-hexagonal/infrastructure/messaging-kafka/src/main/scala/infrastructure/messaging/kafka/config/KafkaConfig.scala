package infrastructure.messaging.kafka.config

case class KafkaConfig(
    bootstrapServers: String,
    groupId: String
)

object KafkaConfig {
  val default: KafkaConfig = KafkaConfig(
    bootstrapServers = sys.env.getOrElse("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"),
    groupId = sys.env.getOrElse("KAFKA_GROUP_ID", "aviation-consumer-group")
  )

  object Topics {
    val routeCreated   = "aviation.route.created"
    val airportUpdated = "aviation.airport.updated"
  }
}
