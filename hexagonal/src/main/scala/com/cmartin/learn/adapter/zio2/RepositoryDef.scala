package com.cmartin.learn.adapter.zio2

import slick.jdbc._
import zio.Task

object RepositoryDef
    extends JdbcProfile
    with JdbcActionComponent.MultipleRowsPerStatementSupport {

  import PersistenceModel._

  trait CountryRepository {
    def findByCode(id: Long): Task[Option[CountryDbo]]
    def findByName(name: String): Task[Option[CountryDbo]]
  }

}
