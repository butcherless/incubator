package com.cmartin.learn

import com.cmartin.learn.Library._
import zio._
import zio.config.typesafe._

object SimpleApp
    extends ZIOAppDefault {

  val logAspect    = ZIOAspect.loggedWith[Int](r => s"sum result: $r")
  val configAspect = ZIOAspect.loggedWith[EnvConfig](cfg => s"environment config: $cfg")
  val fileAspect   = ZIOAspect.loggedWith[FileConfig](file => s"environment config: $file")

  final case class EnvConfig(filename: String, exclusions: List[String])
  final case class FileConfig(filename: String, exclusions: List[String])

  // environment
  val filename: Config[String]         = Config.string("DL_FILENAME")
  val exclusions: Config[List[String]] = Config.listOf(Config.string("DL_EXCLUSIONS"))
  val config: Config[EnvConfig]        =
    (filename ++ exclusions).map { case (f, es) => EnvConfig(f, es) }

  def readFromEnv(): IO[Config.Error, EnvConfig] =
    ConfigProvider
      .envProvider
      .load(config)

  // hocon file
  val hoconFilename: Config[String]         = Config.string("filename")
  val hoconExclusions: Config[List[String]] = Config.listOf(Config.string("exclusions"))
  val hoconConfig: Config[FileConfig]       =
    (hoconFilename ++ hoconExclusions).map { case (f, es) => FileConfig(f, es) }

  def readFromFile(filepath: String) =
    ConfigProvider
      .fromHoconFilePath(filepath)
      .load(hoconConfig)

  def run = {
    for {
      _          <- ZIO.log(echo(TEXT))
      envConfig  <- readFromEnv() @@ configAspect
      fileConfig <- readFromFile("zio-config/src/main/resources/app.conf") @@ fileAspect

      // bug? from version 2.0.11 throws java.lang.InterruptedException
      // _          <- exit(ExitCode(1))
    } yield ()
  }
}
