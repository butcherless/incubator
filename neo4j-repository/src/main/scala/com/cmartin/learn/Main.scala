package com.cmartin.learn

import zio.{LogLevel, RIO, Runtime, Scope, Task, UIO, ZIO, ZIOAppArgs, ZIOAppDefault, ZIOAspect}
import zio.logging.{console, LogAnnotation, LogFormat}
import zio.logging.backend.SLF4J

/* ZIO.scoped use example
 */
object Main extends ZIOAppDefault {

  case class DbSession(session: String) {
    def query(q: String): Task[String] = for {
      _       <- ZIO.log(s"query: $q")
      results <- ZIO.succeed(s"[dummy, results, for, query, '$q']")
    } yield results

    def close(): UIO[Unit] = for {
      _ <- ZIO.log("releasing db connection")
    } yield ()
  }

  trait MyDatabase {
    def openSession(): Task[DbSession]
  }

  case class MyDummyDatabase() extends MyDatabase {
    override def openSession(): Task[DbSession] = for {
      _       <- ZIO.log("acquiring db connection")
      session <- ZIO.succeed(DbSession("dummy-session"))
    } yield session
  }

  val db: MyDummyDatabase = MyDummyDatabase()

  def acquire(db: MyDatabase): Task[DbSession] = db.openSession()

  // scoped resource
  val scopedDb: RIO[Scope, DbSession] = ZIO.acquireRelease(acquire(db))(_.close())

  def query(q: String): Task[String] = ZIO.scoped {
    scopedDb.flatMap(_.query(q))
  }

  val logger = Runtime.addLogger(
    SLF4J.slf4jLogger(SLF4J.logFormatDefault, SLF4J.getLoggerName())
  )

  val loggerLayer = zio.Runtime.removeDefaultLoggers >>> logger

  override def run =
    (for {
      _       <- ZIO.log("default log")
      _       <- ZIO.logDebug("debug log")
      _       <- ZIO.logInfo("info log")
      _       <- ZIO.logError("error log")
      results <- query("select from countries")
      _       <- ZIO.log(s"results: $results")
    } yield ()).provide(loggerLayer)

}
