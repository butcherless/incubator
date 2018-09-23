package com.cmartin.learn.app

import com.cmartin.learn.logic._
import com.cmartin.learn.service.spec._
import com.typesafe.scalalogging.Logger

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class MainApp

object MainApp extends App {
  lazy val logger = Logger[MainApp]
  lazy val initInstant = getTimestamp()
  logger.info(s"MainApp starts at: ${initInstant} instant")

  val gav: Future[Option[GAV]] = getGav(1)

  val result: Option[GAV] = Await.result(gav, 2.second)

  logger.info(s"MainApp stops after: ${getTime(initInstant)} ms")
}
