package com.cmartin.learn.web

import java.time.{Clock, LocalDateTime}

import upickle.default.{macroRW, _}

object ApiModel {

  case class Book(author: String, title: String, year: Int)


  object Book {
    implicit val rw: ReadWriter[Book] = macroRW
  }

  case class BuildInfo(appName: String, date: String, version:String)

  object BuildInfo{
    implicit val rw: ReadWriter[BuildInfo] = macroRW
  }

  val bookExample = Book("HealthInfo", "Dummy", 2020)

  def buildInfo = BuildInfo("incubator web application", LocalDateTime.now(Clock.systemDefaultZone()).toString, "1.0.0-SNAPSHOT")
}
