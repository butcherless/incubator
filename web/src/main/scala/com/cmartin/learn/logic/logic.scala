package com.cmartin.learn

import com.cmartin.learn.app.MainApp
import com.cmartin.learn.service.spec.{GAV, Library}
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

package object logic {
  lazy val logger = Logger[MainApp]

  def getTimestamp() = System.currentTimeMillis()

  def getTime(init: Long) = getTimestamp() - init

  def delayUpToSecs(secs: Int) = Random.nextInt(1000 * secs)

  def buildGav(patch: Int) = GAV("org.scala-sbt", "sbt", s"1.2.$patch")

  def getGav(patch: Int): Future[Option[GAV]] = {
    if (patch >= 0)
      Future {
        val d = delayUpToSecs(1)
        Thread.sleep(d)
        logger.debug(s"I'm $patch, wait $d")
        Some(buildGav(patch))
      } else
      Future {
        None
      }
  }

  def processResults(list: List[Library]) = {
    list.map(l => logger.trace(l.toString))
  }

}
