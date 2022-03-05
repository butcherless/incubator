package com.cmartin.learn

import org.neo4j.driver._
import org.neo4j.driver.async.AsyncSession
import org.neo4j.driver.async.ResultCursor
import zio._

import java.security.Provider.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import scala.concurrent.Future

object Touchdown {
  // Create a managed driver

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

  val uri       = "my-uri"
  val user      = "my-user"
  val pass      = "my-pass"
  val authToken = AuthTokens.basic(user, pass)

  def acquire(uri: String, authTokens: AuthToken): Driver =
    GraphDatabase.driver(uri, authTokens)
  def release(driver: Driver): Unit                       =
    driver.close()

  val readPersonByNameQuery: String =
    """MATCH (p:Person)
        WHERE p.name = $person_name
        RETURN p.name AS name"""
  val params                        = Map("person_name" -> "personName")

  val managedDriver: TaskManaged[Driver] =
    ZManaged.acquireReleaseAttemptWith(acquire(uri, authToken))(release)

  val s1: Session      = ???
  val r1               = s1.run(readPersonByNameQuery)
  val s2: AsyncSession = ???
  val r2               = s2.runAsync(readPersonByNameQuery)

  val rec1  = r1.single()
  val data1 = rec1.get("name").asString()

  val driver1: Driver          = ???
  val fut1                     = driver1.asyncSession().runAsync(readPersonByNameQuery).toCompletableFuture()
  val zio1: Task[ResultCursor] = ZIO.fromCompletableFuture(fut1)

  val zio2 = managedDriver.use { driver =>
    ZIO.fromCompletableFuture(
      driver.asyncSession()
        .runAsync(readPersonByNameQuery)
        .toCompletableFuture()
    )
  }

  val query: RIO[Driver, ResultCursor] = for {
    driver  <- ZIO.service[Driver]
    session <- UIO.succeed(driver.session())
    results <- managedDriver.use { driver =>
                 ZIO.fromCompletableFuture(
                   driver.asyncSession()
                     .runAsync(readPersonByNameQuery)
                     .toCompletableFuture()
                 )
               }
  } yield results

  def acquireSession(driver: Driver): Session =
    driver.session()
  def closeSession(session: Session): Unit    =
    session.close()

  val managedSession: TaskManaged[Session] =
    ZManaged.acquireReleaseAttemptWith(acquireSession(driver1))(closeSession)

  sealed trait DatabaseError
  case class FieldMappingError(message: String)    extends DatabaseError
  case class DefaultDatabaseError(message: String) extends DatabaseError

  trait CountryRepository {
    def findByCode(code: String): IO[DatabaseError, Country]
  }

  case class Neo4jCountryRepository(driver: Driver) extends CountryRepository {
    val managedSession: Managed[DatabaseError, Session] =
      ZManaged.acquireReleaseAttemptWith(acquireSession(driver))(closeSession)
        .mapError(e => DefaultDatabaseError(e.getMessage()))

    override def findByCode(code: String): IO[DatabaseError, Country] = {
      managedSession.use { session =>
        val x = for {
          record <- Task.attempt(session.run(readPersonByNameQuery).single())
          tuple  <- extractCountryProps(record)
        } yield Country(tuple._1, tuple._2)

        x.mapError(e => manageError(e, s"findByCode($code)"))
      }
    }

    /* Country helpers
        - Country extractor
        - Country database field names
     */
    def extractCountryProps(record: Record): Task[(String, String)] = {
      (Task.attempt(record.get("code").asString()) <&>
        Task.attempt(record.get("name").asString()))
    }

    // TODO pattern matching for details
    def manageError(th: Throwable, message: String) = {
      DefaultDatabaseError(s"$message - ${th.getMessage()}")
    }

  }

}
