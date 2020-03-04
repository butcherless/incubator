package com.cmartin.learn

import com.cmartin.learn.Library.Gav
import zio.{Has, Task, ZIO, ZLayer}

package object filemanager {

  type FileManager = Has[FileManager.Service]

  object FileManager {

    trait Service {
      def getLinesFromFile(filename: String): Task[List[String]]

      def logDepCollection(dependencies: List[Either[String, Gav]]): Task[Unit]

      def logMessage(message: String): Task[Unit]

      def logPairCollection(collection: List[String]): Task[Unit]
    }

    val dummyFileManager: ZLayer.NoDeps[Nothing, FileManager] =
      ZLayer.succeed(
        new Service {
          override def getLinesFromFile(filename: String): Task[List[String]] =
            Task.effectTotal(List("line-1", "line-2"))

          override def logDepCollection(dependencies: List[Either[String, Gav]]): Task[Unit] =
            Task.effectTotal(())

          override def logMessage(message: String): Task[Unit] =
            Task.effectTotal(())

          override def logPairCollection(collection: List[String]): Task[Unit] =
            Task.effectTotal(())
        }
      )

    // accessors methods
    def getLinesFromFile(filename: String): ZIO[FileManager, Throwable, List[String]] =
      ZIO.accessM(_.get.getLinesFromFile(filename))

    def logDepCollection(
        dependencies: List[Either[String, Gav]]
    ): ZIO[FileManager, Throwable, Unit] =
      ZIO.accessM(_.get.logDepCollection(dependencies))

    def logMessage(message: String): ZIO[FileManager, Throwable, Unit] =
      ZIO.accessM(_.get.logMessage(message))

    def logPairCollection(collection: List[String]): ZIO[FileManager, Throwable, Unit] =
      ZIO.accessM(_.get.logPairCollection(collection))
  }

}
