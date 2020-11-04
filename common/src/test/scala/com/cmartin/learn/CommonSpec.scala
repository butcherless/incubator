package com.cmartin.learn

import com.cmartin.learn.CommonImplicits._
import com.cmartin.learn.common.sayHello
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CommonSpec extends AnyFlatSpec with Matchers {
  "Dummy test" should "pass" in {
    sayHello() shouldEqual "hello from common"
  }

  it should "T03 tuple2 enhancements" in {
    val text   = "scala"
    val number = 7

    val t = (text, number)

    val first = t.first
    val right = t.right

    assert(first == text)
    assert(right == number)
  }

}
