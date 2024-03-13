package com.cmartin.learn.adapter.zio2

import slick.jdbc._
import zio.{Task, ZLayer}

object DatabaseDefinitions {

  object TableNames {
    val airlines        = "AIRLINES"
    val airports        = "AIRPORTS"
    val countries       = "COUNTRIES"
    val fleet           = "FLEET"
    val flights         = "FLIGHTS"
    val journeys        = "JOURNEYS"
    val routes          = "ROUTES"
    val aircraftJourney = "AIRCRAFT_JOURNEY"
  }

  object AbstractTable
      extends JdbcProfile
      with JdbcActionComponent.MultipleRowsPerStatementSupport {
    import PersistenceModel.LongDbo
    import api._

    abstract class LongBasedTable[T <: LongDbo](tag: Tag, tableName: String)
        extends Table[T](tag, tableName) {
      /* primary key column */
      def id: Rep[Long] =
        column[Long]("ID", O.PrimaryKey, O.AutoInc)
    }
  }

  object AbstracRepository
      extends JdbcProfile
      with JdbcActionComponent.MultipleRowsPerStatementSupport {
    import api._
    import AbstractTable._
    import Helpers.SlickToZioSyntax.fromDBIO
    import PersistenceModel.LongDbo

    abstract class AbstractLongRepository[E <: LongDbo, T <: LongBasedTable[E]](db: JdbcBackend#JdbcDatabaseDef) {
      val entities: TableQuery[T]

      def findById(id: Option[Long]): Task[Option[E]] = {
        val x = entities.filter(_.id === id).result.headOption

        fromDBIO(x)
          .provide(ZLayer.succeed(db))
      }
    }

  }
}
