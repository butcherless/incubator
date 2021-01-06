package com.cmartin.learn.poc

import java.util.UUID

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.cmartin.learn.domain.Model.Airport2
import com.cmartin.learn.domain.Model.Country
import io.getquill.Literal
import io.getquill.NamingStrategy
import io.getquill.PostgresAsyncContext
import io.getquill.PostgresDialect
import io.getquill.context.Context
import io.getquill.idiom.Idiom
import izumi.reflect.macrortti.LTag.Weak

object Abstractions {

  trait BaseEntity[I] {
    val id: I
  }

  // Domain Port
  trait BaseRepository[F[_], E] {
    def insert(e: E): F[E]
    def update(e: E): F[E]
    def delete(e: E): F[E]
  }

  abstract class LongEntity(id: Long) extends BaseEntity[Long]
  abstract class UuidEntity(id: UUID) extends BaseEntity[UUID]
}

object Implementations {
  import Abstractions._

  /* C L A S S E S */

  trait Converter {
    def toModel[A, B](a: A): B
  }
  case class CountryDbo(name: String, code: String, id: Long = 0L) extends LongEntity(id)
  object CountryDbo {
    def fromCountry(country: Country): CountryDbo =
      CountryDbo(
        name = country.name,
        code = country.code
      )

    def toModel(dbo: CountryDbo): Country =
      Country(
        name = dbo.name,
        code = dbo.code
      )
  }

  case class AirportDbo(
      name: String,
      iataCode: String,
      icaoCode: String,
      countryId: Long,
      id: Long = 0L
  ) extends LongEntity(id)

  object AirportDbo {
    def from(airport: Airport2, countryId: Long): AirportDbo =
      AirportDbo(
        name = airport.name,
        iataCode = airport.iataCode,
        icaoCode = airport.icaoCode,
        countryId = countryId
      )
    def toModel(dbo: AirportDbo, c: CountryDbo): Airport2 =
      Airport2(
        name = dbo.name,
        iataCode = dbo.iataCode,
        icaoCode = dbo.icaoCode,
        country = CountryDbo.toModel(c)
      )
  }

  case class RepositoryException(m: String) extends RuntimeException(m)

  /* C O N T E X T S */

  trait CommonContext[I <: Idiom, N <: NamingStrategy] { this: Context[I, N] =>
    def findCountryByCodeQuery(code: String) =
      quote {
        query[CountryDbo]
          .filter(c => c.code == lift(code))
      }
  }

  trait CountryDboContext[I <: Idiom, N <: NamingStrategy] extends CommonContext[I, N] {
    this: Context[I, N] =>

    def insertQuery(dbo: CountryDbo) =
      quote {
        query[CountryDbo]
          .insert(lift(dbo))
      }

    def updateQuery(dbo: CountryDbo) =
      quote {
        query[CountryDbo]
          .filter(c => c.id == lift(dbo.id))
          .update(lift(CountryDbo(dbo.name, dbo.code, dbo.id)))
      }

    def deleteQuery(dbo: CountryDbo) =
      quote {
        query[CountryDbo]
          .filter(c => c.id == lift(dbo.id))
          .delete
      }

  }

  trait AirportDboContext[I <: Idiom, N <: NamingStrategy] extends CommonContext[I, N] {
    this: Context[I, N] =>
    def insertQuery(dbo: AirportDbo) =
      quote {
        query[AirportDbo]
          .insert(lift(dbo))
      }

    def findByCountryCodeQuery(code: String) =
      quote {
        for {
          country <- query[CountryDbo]
            .filter(c => c.code == lift(code))
          airports <- query[AirportDbo]
            .join(airport => airport.countryId == country.id)

        } yield (airports, country)
      }

  }

  /* Domain Port
     TODO refactor: Future => zio.Task
   */
  trait CountryRepository extends BaseRepository[Future, Country] {
    def findByCode(code: String): Future[Country]
  }

  /* Domain Port
     TODO refactor: Future => zio.Task
   */
  trait AirportRepository extends BaseRepository[Future, Airport2] {
    def findByCountryCode(code: String): Future[Seq[Airport2]]
  }

  class AirportPostgresRepository(configPrefix: String)(implicit ec: ExecutionContext)
      extends CommonPostgresRepository(configPrefix)
      with AirportDboContext[PostgresDialect, Literal]
      with AirportRepository {

    override def insert(airport: Airport2): Future[Airport2] = {
      val program = for {
        countries <- runIO(findCountryByCodeQuery(airport.country.code))
        country   <- checkHeadElement(countries, s"country code not found: ${airport.country.code}")
        dbo       <- IO.successful(AirportDbo.from(airport, country.id))
        _         <- runIO(insertQuery(dbo))
      } yield airport

      performIO(program)
    }

    override def update(e: Airport2): Future[Airport2] = ???

    override def delete(e: Airport2): Future[Airport2] = ???

    override def findByCountryCode(code: String): Future[Seq[Airport2]] = {
      val program = for {
        dbos <- runIO(findByCountryCodeQuery(code))
        airports <- IO.successful(
          dbos.map(tuple => AirportDbo.toModel(tuple._1, tuple._2))
        )
      } yield airports

      performIO(program)
    }

  }

  class CommonPostgresRepository(configPrefix: String)
      extends PostgresAsyncContext[Literal](Literal, configPrefix) {
    def checkHeadElement[T](seq: Seq[T], error: String) = {
      seq.headOption
        .fold(
          IO.failed[T](RepositoryException(error))
        )(e => IO.successful(e))
    }

  }

  class CountryPostgresRepository(configPrefix: String)(implicit ec: ExecutionContext)
      extends CommonPostgresRepository(configPrefix)
      with CountryDboContext[PostgresDialect, Literal]
      with CountryRepository {

    override def insert(country: Country): Future[Country] = {
      val dbo = CountryDbo.fromCountry(country)
      val program =
        for {
          _ <- runIO(insertQuery(dbo))
        } yield country

      performIO(program)
    }

    override def update(country: Country): Future[Country] = {
      val program =
        for {
          dbos <- runIO(findCountryByCodeQuery(country.code))
          dbo  <- checkHeadElement(dbos, s"country code not found: ${country.code}}")
          _    <- runIO(updateQuery(dbo))
        } yield country

      performIO(program)
    }

    override def delete(country: Country): Future[Country] = {
      val program =
        for {
          dbos <- runIO(findCountryByCodeQuery(country.code))
          dbo  <- checkHeadElement(dbos, s"country code not found: ${country.code}}")
          _    <- runIO(deleteQuery(dbo))
        } yield country

      performIO(program)
    }

    /* cases:
       - Dbolist is empty => Entity not found error
       - DboList has a single Entity => database index constraint
     */
    override def findByCode(code: String): Future[Country] = {
      val program = for {
        dbos    <- runIO(findCountryByCodeQuery(code))
        dbo     <- checkHeadElement(dbos, s"country code not found: $code")
        country <- IO.successful(CountryDbo.toModel(dbo))
      } yield country

      performIO(program)
    }

  }

}
