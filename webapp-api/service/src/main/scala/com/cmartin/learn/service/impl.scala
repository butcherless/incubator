package com.cmartin.learn.service

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import com.cmartin.learn.common.sayHello
import com.cmartin.learn.repository.spec.DummyRepository
import com.cmartin.learn.service.spec.DummyService

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

package object impl {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val uri = Uri("https://search.maven.org/solrsearch/select?q=g:com.typesafe.play%20AND%20a:play-json_2.12&core=gav")

  class DummyServiceImpl(repository: DummyRepository) extends DummyService {
    override def operationOne(): String = {
      repository.saveDummy()
      sayHello()
    }

    override def searchKey(json: String, key: String): Unit = {
      // Option[String] = {

      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = uri))
      Await.result(responseFuture, Duration(5000, TimeUnit.MILLISECONDS));

      val res = responseFuture.onComplete {
        case Success(res) => println(s"success: ${res.entity.dataBytes}")
        case Failure(_) => println("failure")
      }

    }
  }

}

