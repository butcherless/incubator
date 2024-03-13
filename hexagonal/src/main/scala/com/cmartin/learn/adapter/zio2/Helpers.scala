package com.cmartin.learn.adapter.zio2

import slick.jdbc._
import zio.{RIO, ZIO}

object Helpers extends JdbcProfile with JdbcActionComponent.MultipleRowsPerStatementSupport {

  object SlickToZioSyntax
      extends JdbcProfile
      with JdbcActionComponent.MultipleRowsPerStatementSupport {
    import api._

    def fromDBIO[R](dbio: => DBIO[R]): RIO[JdbcBackend#JdbcDatabaseDef, R] = for {
      db <- ZIO.service[JdbcBackend#JdbcDatabaseDef]
      r  <- ZIO.fromFuture(_ => db.run(dbio))
    } yield r
  }
}
