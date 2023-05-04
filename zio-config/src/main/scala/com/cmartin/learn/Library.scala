package com.cmartin.learn

import zio._

object Library {

  val TEXT = "simple-application-hello"

  def echo(message: String): String = {
    message
  }

  def sum(a: Int, b: Int): UIO[Int] = {
    ZIO.succeed(a + b)
  }

}
