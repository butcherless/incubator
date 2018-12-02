package com.cmartin.learn.repository

import com.cmartin.learn.repository.poc._
import org.scalatest._


class PocSpec extends FlatSpec with Matchers {

  it should "return the same value" in {
    val inputValue = 1
    val result = echo(inputValue)
    result shouldBe inputValue
  }

}