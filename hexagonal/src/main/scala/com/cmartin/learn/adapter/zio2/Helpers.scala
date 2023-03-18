package com.cmartin.learn.adapter.zio2

import slick.jdbc._
import zio.{ RIO, ZIO }

object Helpers extends JdbcProfile {

  object SlickToZioSyntax
      extends JdbcProfile {
    import api._

    def fromDBIO[R](dbio: => DBIO[R]): RIO[JdbcBackend#DatabaseDef, R] = for {
      db <- ZIO.service[JdbcBackend#DatabaseDef]
      r  <- ZIO.fromFuture(_ => db.run(dbio))
    } yield r
  }
}
