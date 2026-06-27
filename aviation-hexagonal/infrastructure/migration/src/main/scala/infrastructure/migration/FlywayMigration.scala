package infrastructure.migration

import org.flywaydb.core.Flyway
import zio.*

object FlywayMigration {

  def migrate(url: String, user: String, password: String): Task[Unit] =
    ZIO.attemptBlocking {
      val result = Flyway
        .configure()
        .dataSource(url, user, password)
        .locations("classpath:db/migration")
        .load()
        .migrate()
      ZIO.logInfo(s"Flyway: applied ${result.migrationsExecuted} migration(s)")
    }.flatten

  val layer: TaskLayer[Unit] =
    ZLayer.fromZIO {
      migrate(
        url = sys.env.getOrElse("POSTGRES_URL", "jdbc:postgresql://localhost:5432/aviation"),
        user = sys.env.getOrElse("POSTGRES_USER", "aviation"),
        password = sys.env.getOrElse("POSTGRES_PASSWORD", "aviation")
      )
    }
}
