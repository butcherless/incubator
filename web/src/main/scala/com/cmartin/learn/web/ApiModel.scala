package com.cmartin.learn.web

import java.time.{Clock, LocalDateTime}

import upickle.default.{macroRW, _}

object ApiModel {

  case class Book(author: String, title: String, year: Int)

  object Book {
    implicit val rw: ReadWriter[Book] = macroRW
  }

  case class BuildInfo(appName: String, date: String, version: String, result: Result)

  object BuildInfo {
    implicit val rw: ReadWriter[BuildInfo] = macroRW
  }

  sealed trait Result
  object Success extends Result
  object Warning extends Result
  object Error   extends Result

  object Result {
    implicit val rw: ReadWriter[Result] = macroRW
  }

  /*
    API Objects examples
   */

  val bookExample = Book("Pedro Muñoz Seca", "La venganza de Don Mendo", 1918)

  def buildInfo(): BuildInfo =
    BuildInfo(
      "incubator web application",
      LocalDateTime.now(Clock.systemDefaultZone()).toString,
      "1.0.0-SNAPSHOT",
      Success
    )
}
