package com.cmartin.learn.repository

package object poc {

  def echo(x: Int) = x


  trait Entity[T, I] {
    val id: Option[I]
  }

  case class Fruit(name: String, override val id: Option[Long]) extends Entity[Fruit, Long]

  /*
      POC
   */

  trait SimpleRepository[M[_], T, K] {

    def findAll(filter: (T) => Boolean): M[List[T]]

    def findById(k: K): M[T]

    def remove(t: T): M[K]

    def removeAll(filter: (T) => Boolean): M[List[K]]

    def save(t: T): M[K]

    def count(): M[Long]
  }
}
