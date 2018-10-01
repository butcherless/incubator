package com.cmartin.learn

import com.cmartin.learn.common.sayHello
import com.cmartin.learn.repository.impl.{Aircraft, MemoryRepository}
import org.scalatest.{FlatSpec, Matchers}

class RepositorySpec extends FlatSpec with Matchers {


  def fixture = new {
    lazy val repository = MemoryRepository()
  }


  "Dummy test" should "pass" in {
    sayHello() shouldEqual "hello from common"
  }

  ignore should "save" in {
    val c = fixture.repository.count()
    val a = Aircraft(0, "B787", "ec-mab")
    val aSaved = fixture.repository.save(a)

    fixture.repository.count() shouldBe 1
  }


}
