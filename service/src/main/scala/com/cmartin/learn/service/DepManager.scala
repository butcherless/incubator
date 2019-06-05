package com.cmartin.learn.service

import com.typesafe.config.{ Config, ConfigFactory }
import com.typesafe.scalalogging.Logger

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Failure

object Test {

  def testFuture() = {
    import ExecutionContext.Implicits.global
    val f = Future("hello")
    val res = f.onComplete {
      case scala.util.Success(value) => "success"
      case Failure(exception) => "failure"
    }
  }
}

class DepManager

case class Settings(host: String, port: Int, repo: String)

//println(s"insert into tdplibrary values('${l.g}','${l.a}','${l.v}','${l.d}','${l.s}')")
object DepManager extends App {
  lazy val logger = Logger[DepManager]

  // http get request to nexus
  val artifactName = "scs-multicanal-perfilado"
  val repositoryName = "mutua-releases-lib"

  def init(): Settings = {
    val config: Config = ConfigFactory.load()
    val settings = Settings(
      config.getString("nexus.host"),
      config.getInt("nexus.port"),
      config.getString("nexus.repo"))
    logger.info(settings.toString)
    settings
  }

  logger.info("application stopped.")
}
