package com.cmartin.learn

import com.cmartin.learn.Library._
import zio.test.Assertion._
import zio.test._

object ZioSpec
  extends DefaultRunnableSpec {

  def spec = suite("my spec")(
    test("my test")
    (assert(1 + 1)
    (
      equalTo(2))
    ),

    test("Echo function return the same text")
    (assert(echo(TEXT))
    (
      equalTo(TEXT))
    ),

    testM("Zio effect sum 2 + 3")
    (
      for {
        r <- sum(2, 3)
      } yield assert(r)(equalTo(5))
    )

  )
}

