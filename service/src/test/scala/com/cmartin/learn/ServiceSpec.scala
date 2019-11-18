package com.cmartin.learn

import com.cmartin.learn.common.sayHello
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ServiceSpec extends AnyFlatSpec with Matchers {
  // TODO add Mock
  //val service = new DummyServiceImpl(repository)

  "Dummy test" should "pass" in {
    sayHello() shouldEqual "hello from common"
  }

  "Searching json key" should "return expected value" in {
    //val res = service.searchKey("", "")

    //res shouldBe Some
  }
}
