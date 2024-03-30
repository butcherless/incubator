package com.cmartin.learn

import org.neo4j.driver._
import org.neo4j.driver.async.AsyncSession
import org.neo4j.driver.async.ResultCursor
import zio.Runtime.{default => runtime}
import zio.ZLayer.Debug
import zio._
import zio.stream.ZStream
import scala.jdk.CollectionConverters._

object Touchdown {
  // Create a scoped driver

  /** Country object
    * @param name
    *   country name
    * @param code
    *   business identifier
    */
  case class Country(
      name: String,
      code: String
  )

  case class DatabaseInput(uri: String, authTokens: AuthToken)

  val uri        = "my-uri"
  val user       = "my-user"
  val pass       = "my-pass"
  val authTokens = AuthTokens.basic(user, pass)
  val dbInput    = DatabaseInput(uri, authTokens)

  def acquire(input: DatabaseInput): Task[Driver] =
    ZIO.attempt(GraphDatabase.driver(input.uri, input.authTokens))

  def release(driver: Driver): UIO[Unit] =
    ZIO.succeed(driver.close())

  object Queries {
    val readPersonByNameQuery: String =
      """MATCH (p:Person)
        WHERE p.name = $person_name
        RETURN p.name AS name"""
  }
  val params = Map("person_name" -> "personName")

  val scopedDriver: RIO[Scope, Driver] =
    ZIO.acquireRelease(acquire(dbInput))(release)

  val scopedStreamDriver =
    ZStream
      .acquireReleaseWith(
        acquire(dbInput)
      )(release)

  val s1: Session      = ???
  val r1               = s1.run(Queries.readPersonByNameQuery)
  val s2: AsyncSession = ???
  val r2               = s2.runAsync(Queries.readPersonByNameQuery)

  val rec1  = r1.single()
  val data1 = rec1.get("name").asString()

  val driver1: Driver = ???
  val resList         = driver1.session().run(Queries.readPersonByNameQuery).list().asScala.toList

  val list: ZIO[Scope, Throwable, List[Record]] =
    scopedDriver
      .flatMap { driver =>
        ZIO.attempt(
          driver1.session()
            .run(Queries.readPersonByNameQuery)
            .list()
            .asScala.toList
        )
      }

  val stream: ZStream[Any, Throwable, Record] =
    scopedStreamDriver
      .flatMap { driver =>
        ZStream.fromJavaStream(
          driver1.session()
            .run(Queries.readPersonByNameQuery)
            .stream()
        )
      }

  val listQuery: ZIO[Driver, Throwable, List[Record]] =
    for {
      results <- ZIO.scoped(list)
    } yield results

  val streamQuery =
    for {
      results <- ZStream.from(stream)
    } yield results

  def acquireSession(driver: Driver): Task[Session] =
    ZIO.attempt(driver.session())
  def closeSession(session: Session): UIO[Unit]     =
    ZIO.succeed(session.close())

  val scopedSession: RIO[Scope, Session] =
    ZIO.acquireRelease(acquireSession(driver1))(closeSession)

  val l1 = ZLayer(ZIO.acquireRelease(acquireSession(driver1))(closeSession))
  val l3 = ZLayer.scoped(scopedSession)
  val l2 = ZLayer.fromZIO(scopedSession)

  sealed trait DatabaseError
  case class FieldMappingError(message: String)    extends DatabaseError
  case class DefaultDatabaseError(message: String) extends DatabaseError

  trait CountryRepository {
    def findByCode(code: String): IO[DatabaseError, Country]
  }

  case class Neo4jCountryRepository(driver: Driver) extends CountryRepository {
    val scopedSession: ZIO[Scope, DefaultDatabaseError, Session] =
      ZIO.acquireRelease(acquireSession(driver))(closeSession)
        .mapError(e => DefaultDatabaseError(e.getMessage))

    override def findByCode(code: String): IO[DatabaseError, Country] =
      ZIO.scoped {
        scopedSession.flatMap { session =>
          ZIO.attempt(session.run(Queries.readPersonByNameQuery).single())
            .flatMap(extractCountry)
            .mapError(e => manageError(e, s"findByCode($code)"))
        }
      }

    /* Country helpers
        - Country extractor
        - Country database field names
     */
    def extractCountryProps(record: Record): Task[(String, String)] = {
      ZIO.attempt(record.get("code").asString()) <&>
        ZIO.attempt(record.get("name").asString())
    }
  }

  object Neo4jCountryRepository {
    val live: URLayer[Driver, CountryRepository] =
      ZLayer.fromFunction(d => Neo4jCountryRepository(d))
  }

  // add pattern matching for error details if needed
  def manageError(th: Throwable, message: String): DatabaseError =
    DefaultDatabaseError(s"$message - ${th.getMessage}")

  def extractCountry(record: Record): Task[Country] =
    (ZIO.attempt(record.get("code").asString()) <&>
      ZIO.attempt(record.get("name").asString()))
      .map(Country.tupled)

  object Main {

    val driverLayer: TaskLayer[Driver] =
      ZLayer.scoped(ZIO.acquireRelease(acquire(dbInput))(release))

    val dbLayer: TaskLayer[CountryRepository with Driver] =
      ZLayer.make[CountryRepository with Driver](
        driverLayer,
        Neo4jCountryRepository.live,
        Debug.mermaid
      )

    val dbProgram: ZIO[CountryRepository, DatabaseError, Country] = for {
      repo    <- ZIO.service[CountryRepository]
      country <- repo.findByCode("es")
    } yield country

    val dbResult: Country = Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(
        dbProgram.provide(dbLayer)
      ).getOrThrowFiberFailure()
    }
  }

}
