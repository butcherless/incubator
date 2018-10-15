package com.cmartin.learn

import com.cmartin.learn.service.spec.GAV
import org.scalatest.OptionValues._
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Random

class FutureSpec extends AsyncFlatSpec with Matchers {

  val gav1 = GAV("com.typesafe", "config", "1.3.3")

  "Get artifact future patch >= 0" should "return Some" in {
    val artifact = getArtifact(2)
    artifact map (t => t.value.version shouldEqual ("1.3.2"))
  }

  "Get artifact future patch < 0" should "return None" in {
    val artifact = getArtifact(-1)
    artifact map (t => t shouldBe None)
  }


  "future for comprehension" should "aggregate results" in {
    val f1: Future[Option[GAV]] = getArtifact(1)
    val f2 = getArtifact(2)
    val f3 = getArtifact(3)

    val tuple: Future[(Option[GAV], Option[GAV], Option[GAV])] = for {
      r1 <- f1
      r2 <- f2
      r3 <- f3
    } yield (r1, r2, r3)

    tuple map (t => {
      t._1.value.version shouldBe "1.3.1"
      t._2.value.version shouldBe "1.3.2"
      t._3.value.version shouldBe "1.3.3"
    })
  }

  /*

  "Get artifact list future" should "return a result list" in {
    val futureOperations = List(
      getArtifact(0),
      getArtifact(1),
      getArtifact(-1),
      getArtifact(3),
      getArtifact(4)
    )

    val futureTraverseResult: Future[List[Option[GAV]]] = Future.traverse(futureOperations) {
      e => e
    }

    //    val futureTraverseResult: Future[List[Option[GAV]]] = Future.sequence(futureOperations)

    futureTraverseResult.onComplete {
      case Success(results) => println(s"Results (${results.size}): $results")
      case Failure(e) => println(s"Error processing future operations, error = ${e.getMessage}")
    }

    val result: List[Option[GAV]] = Await.result(futureTraverseResult, 10 seconds)
  }

  */

  /*
  *  H E L P E R S
  */


  def getArtifact(patch: Int): Future[Option[GAV]] = {
    val d = delay(1)
    if (patch >= 0)
      Future {
        //        println(s"I'm $patch, wait $d")
        Thread.sleep(d)
        Some(gav1.copy(version = s"1.3.$patch"))
      }
    else Future {
      //        println(s"I'm $patch, wait $d")
      Thread.sleep(d)
      None
    }
  }

  def delay(secs: Int) = Random.nextInt(1000 * secs)
}
