package com.cmartin.learn

import com.cmartin.learn.poc.{Message, User}
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class PocSpec extends FlatSpec with Matchers with OptionValues {

  val userName = "cmartin"
  val userId = Some(7L)
  val text = "message text"

  it should "test user entity with default id" in {
    val user = User(userName)

    user.name shouldBe userName
    user.id shouldBe None
  }

  it should "test user entity" in {
    val user = User(userName, userId)

    user.name shouldBe userName
    user.id shouldBe userId
  }

  it should "test message entity" in {
    val message = Message(text, userId.getOrElse(0L))

    message.text shouldBe text
    message.userId shouldBe userId.value
  }

}
