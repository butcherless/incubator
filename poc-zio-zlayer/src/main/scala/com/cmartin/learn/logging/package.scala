package com.cmartin.learn

import zio.{Has, UIO, ZIO, ZLayer}

package object logging {

  type Logging = Has[Logging.Service]

  object Logging {

    trait Service {
      def info(message: String): UIO[Unit]

      def error(message: String): UIO[Unit]
    }

    import zio.console.Console

    val consoleLogger: ZLayer[Console, Nothing, Logging] =
      ZLayer.fromFunction(console =>
        new Service {
          override def info(message: String): UIO[Unit] =
            console.get.putStrLn(s"info - $message")

          override def error(message: String): UIO[Unit] =
            console.get.putStrLn(s"error - $message")
        }
      )

    // accessors methods
    def info(message: String): ZIO[Logging, Nothing, Unit] =
      ZIO.accessM(_.get.info(message))

    def error(message: String): ZIO[Logging, Nothing, Unit] =
      ZIO.accessM(_.get.error(message))

  }

}
