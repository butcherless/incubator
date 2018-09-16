package com.cmartin.learn

import com.cmartin.learn.service.spec.GAV
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class FutureSpec extends FlatSpec with Matchers {

  val gav1 = GAV("com.typesafe", "config", "1.3.3")


  "Get artifact future patch >= 0" should "return Some" in {
    val result: Option[GAV] = Await.result(getArtifact(2), 10 seconds)
    result.isDefined shouldBe (true)
    result.get.version shouldEqual ("1.3.2")
  }

  "Get artifact future patch < 0" should "return None" in {
    val result: Option[GAV] = Await.result(getArtifact(-1), 10 seconds)
    result.isEmpty shouldBe (true)
  }

  "Get artifact list future" should "return a result list" in {
    val futureOperations = List(
      getArtifact(0),
      getArtifact(1),
      getArtifact(2),
      getArtifact(3),
      getArtifact(4)
    )

    val futureTraverseResult: Future[List[Option[GAV]]] = Future.traverse(futureOperations) {
      e => e
    }

    futureTraverseResult.onComplete {
      case Success(results) => println(s"Results $results")
      case Failure(e) => println(s"Error processing future operations, error = ${e.getMessage}")
    }
  }

  // HELPERS

  def getArtifact(patch: Int): Future[Option[GAV]] = {
    if (patch >= 0)
      Future {
        Some(gav1.copy(version = s"1.3.$patch"))
      }
    else Future {
      None
    }
  }

}
