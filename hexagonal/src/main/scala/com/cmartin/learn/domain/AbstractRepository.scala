package com.cmartin.learn.domain

trait AbstractRepository[F[_], E] {
  def insert(e: E): F[E]

  def findById(id: Long): F[Option[E]]
}
