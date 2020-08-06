package com.cmartin.learn.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl._
import akka.util.ByteString
import com.typesafe.scalalogging.Logger

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.Random

/*
    How to test it: curl --limit-rate 8192b 127.0.0.1:8080/random
 */

object WebServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val logger: Logger = Logger[WebServer.type]

    // streams are re-usable so we can define it here
    // and use it for every request
    val numbers = Source.fromIterator(() => Iterator.continually(Random.nextInt()))
    var counter = 0

    val route =
      path("random") {
        get {
          complete(
            HttpEntity(
              ContentTypes.`text/plain(UTF-8)`,
              // transform each number to a chunk of bytes
              numbers.map(n => {
                counter = counter + 1
                logger.debug(s"number: $n, counter=$counter")
                ByteString(s"$n\n")
              })
            )
          )
        }
      }

    val bindingFuture: Future[Http.ServerBinding] =
      Http()
        .newServerAt("localhost", 8080)
        .bind(route)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind())                 // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
