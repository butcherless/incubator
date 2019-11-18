package com.cmartin.learn

import com.cmartin.learn.common.sayHello
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ControllerSpec extends AnyFlatSpec with Matchers {
  "Dummy test" should "pass" in {
    sayHello() shouldEqual "hello from common"
  }
}
