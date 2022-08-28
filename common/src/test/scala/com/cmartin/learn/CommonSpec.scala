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

  "Lists" should "concatenate several file lines" in {
    val file1    = List("f1.l1", "f1.l2")
    val file2    = List("f2.l1", "f2.l2", "f2.l3")
    val file3    = List("f3.l1")
    val files    = List(file1, file2, file3)
    val expected = file1 ++ file2 ++ file3

    val result = files.fold(List.empty)((a, b) => a ++ b)

    val reduceResult = files.reduce((a, b) => a ++ b)

    info(s"result: $result")

    result shouldBe expected
    reduceResult shouldBe expected
  }

}
