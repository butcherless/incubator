package com.cmartin.learn

import com.cmartin.learn.CommonImplicits._
import com.cmartin.learn.common.sayHello
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CommonSpec 
extends AnyFlatSpec
with Matchers {

  "Tuple enhancements" should "access to the tuple elements via alias" in {
    val text   = "scala"
    val number = 7

    val t = (text, number)

    val first = t.left
    val right = t.right

    assert(first == text)
    assert(right == number)
  }

}
