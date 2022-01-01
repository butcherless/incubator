package com.cmartin.learn.adapter.quill
import com.cmartin.learn.poc.CommonOpsMacro
import io.getquill._
import io.getquill.context.Context
import io.getquill.monad.IOMonad

import scala.language.experimental.macros //TODO rename package

object QuillRepositoryCommons {
  import Model._
  /* AbstractContext: Query DSL for common operations via macro implementations */
  trait EntityContext extends IOMonad {
    this: Context[_, _] =>

    def insertQuery[T](entity: T): Quoted[Insert[T]] = macro CommonOpsMacro.insert[T]

    def updateQuery[T](entity: T): Quoted[Update[T]] = macro CommonOpsMacro.update[T]

    def deleteQuery[T](entity: T): Quoted[Delete[T]] = macro CommonOpsMacro.delete[T]

    def findCountryByCodeQuery(code: String) =
      quote {
        query[CountryDbo]
          .filter(c => c.code == lift(code))
      }

    def findAirportByIataCodeQuery(code: String) =
      quote {
        query[AirportDbo]
          .filter(a => a.iataCode == lift(code))
      }

    def findAirportByCountryCodeQuery(code: String) =
      quote {
        for {
          c  <- query[CountryDbo] if (c.code == lift(code))
          as <- query[AirportDbo] if (c.id.contains(as.countryId))
        } yield (as, c)
      }

    def checkHeadElement[T](seq: Seq[T], error: String) = {
      seq.headOption
        .fold(
          IO.failed[T](RepositoryException(error))
        )(e => IO.successful(e))
    }
  }

  abstract class AbstractDboRepository(configPrefix: String) {
    val ctx = new PostgresAsyncContext(SnakeCase, configPrefix) with EntityContext
  }

}
