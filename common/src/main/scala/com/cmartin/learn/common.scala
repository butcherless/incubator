package com.cmartin.learn

import scala.util.Random

package object common {
  val dummyCommonVal = 0

  def sayHello() = "hello from common"

  def getDummyInt() = Random.nextInt()

  def getDummyDouble() = Random.nextDouble()

}
