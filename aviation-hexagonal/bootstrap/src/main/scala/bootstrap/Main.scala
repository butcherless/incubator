package bootstrap

import adapter.http.server.HttpServer
import infrastructure.migration.FlywayMigration
import infrastructure.messaging.kafka.relay.OutboxRelay
import zio.*
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault {

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  override def run: ZIO[ZIOAppArgs & Scope, Any, Any] =
    for {
      _ <- ZIO.logInfo("Aviation Hexagonal starting up")
      _ <- FlywayMigration.migrate(
             url = sys.env.getOrElse("POSTGRES_URL", "jdbc:postgresql://localhost:5432/aviation"),
             user = sys.env.getOrElse("POSTGRES_USER", "aviation"),
             password = sys.env.getOrElse("POSTGRES_PASSWORD", "aviation")
           )
      _ <- ZIO.logInfo("Database migrations applied")
      _ <- (HttpServer.serve race OutboxRelay.relay)
        .provide(WiringModule.appLayer)
    } yield ()
}
