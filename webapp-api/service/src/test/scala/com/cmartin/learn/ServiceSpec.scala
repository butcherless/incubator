package com.cmartin.learn

import com.cmartin.learn.common.sayHello
import com.cmartin.learn.repository.impl.DummyRepositoryImpl
import com.cmartin.learn.service.impl.DummyServiceImpl
import org.scalatest.{FlatSpec, Matchers}

class ServiceSpec extends FlatSpec with Matchers {

  // TODO add Mock
  val repository = new DummyRepositoryImpl()
  //val service = new DummyServiceImpl(repository)

  "Dummy test" should "pass" in {
    sayHello() shouldEqual "hello from common"
  }

  "Searching json key" should "return expected value" in {
    //val res = service.searchKey("", "")

    //res shouldBe Some
  }
}
