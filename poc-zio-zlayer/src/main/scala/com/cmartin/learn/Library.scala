package com.cmartin.learn

object Library {

  case class Gav(group: String, artifact: String, version: String)

  import zio.UIO

  val TEXT = "simple-application-hello"

  def echo(message: String): String = {
    message
  }

  def sum(a: Int, b: Int): UIO[Int] = {
    UIO.effectTotal(a + b)
  }

}
