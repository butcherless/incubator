package infrastructure.persistence.postgres.config

import doobie.util.transactor.Transactor
import zio.{Task, TaskLayer}

case class PostgresConfig(
    url: String,
    user: String,
    password: String,
    maxPoolSize: Int
)

object PostgresConfig {

  val default: PostgresConfig = PostgresConfig(
    url = sys.env.getOrElse("POSTGRES_URL", "jdbc:postgresql://localhost:5432/aviation"),
    user = sys.env.getOrElse("POSTGRES_USER", "aviation"),
    password = sys.env.getOrElse("POSTGRES_PASSWORD", "aviation"),
    maxPoolSize = 10
  )

  // TODO: wire up HikariTransactor with proper ZIO scoped resource
  val transactorLayer: TaskLayer[Transactor[Task]] = ???
}
