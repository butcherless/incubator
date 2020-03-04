package com.cmartin.learn

import com.cmartin.learn.Library._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LibrarySpec extends AnyFlatSpec with Matchers {
  "LibrarySpec echo" should "return the same text" in {
    val result = echo(TEXT)

    result shouldBe TEXT
  }
}
