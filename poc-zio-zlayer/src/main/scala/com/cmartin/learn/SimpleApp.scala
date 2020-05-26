package com.cmartin.learn

import com.cmartin.learn.filemanager.FileManager
import com.cmartin.learn.logging.Logging
import zio.console.Console
import zio.{App, ExitCode, Task, ZIO, ZLayer}

object SimpleApp extends App {

  val program = for {
    _ <- Logging.info("program start")
    _ <- FileManager.getLinesFromFile("filename")
    _ <- Logging.info("program end")
  } yield ()

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    // application services
    val serviceDeps: ZLayer[Console, Nothing, Logging with FileManager] =
      Logging.consoleLogger ++ FileManager.dummyFileManager

    // external services injection
    val fullLayer: ZLayer[Any, Nothing, Logging with FileManager] =
      Console.live >>> serviceDeps

    // full runnable program
    val runnable: Task[Unit] = program.provideLayer(fullLayer)

    runnable.fold(
      _ => ExitCode.failure,
      _ => ExitCode.success
    )

  }
}
