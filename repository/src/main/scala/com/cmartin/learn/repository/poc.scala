package com.cmartin.learn

package object poc {

  def echo(x: Int) = x


  trait Entity[T, I] {
    val id: Option[I]
  }

  case class Fruit(name: String, override val id: Option[Long]) extends Entity[Fruit, Long]

}
