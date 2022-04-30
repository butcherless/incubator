package com.cmartin.learn.adapter.zio2

import slick.jdbc._
import zio.Task

object RepositoryImplementation
    extends JdbcProfile {

  import api._
  import Helpers.SlickToZioSyntax.fromDBIO
  import PersistenceModel._
  import CountryTableDef._
  import RepositoryDef._
  import DatabaseDefinitions.AbstracRepository._

  lazy val countries = TableQuery[CountryTable]

  case class SlickCountryRepository(db: JdbcBackend#DatabaseDef)
      extends AbstractLongRepository[CountryDbo, CountryTable](db)
      with CountryRepository {

    override val entities = TableQuery[CountryTable]

    override def findByCode(id: Long): Task[Option[CountryDbo]] = {
      val query = entities.filter(_.id === id).result
      fromDBIO(query)
        .map(_.headOption)
        .provideService(db)
    }

    override def findByName(name: String): Task[Option[CountryDbo]] = {
      val query = entities.filter(_.name === name).result
      fromDBIO(query)
        .map(_.headOption)
        .provideService(db)
    }

  }

}
