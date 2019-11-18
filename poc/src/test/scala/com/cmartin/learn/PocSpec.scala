package com.cmartin.learn

import com.cmartin.learn.repository.definition.echo
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PocSpec extends AnyFlatSpec with Matchers {
  it should "return the same value" in {
    val inputValue = 1
    val result     = echo(inputValue)
    result shouldBe inputValue
  }
}
