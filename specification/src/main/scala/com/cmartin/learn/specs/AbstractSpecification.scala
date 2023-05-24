package com.cmartin.learn.specs

/** Abstract base implementation of composite Specification with default
  * implementations for and, or and not.
  */

abstract class AbstractSpecification[T]
    extends Specification[T] {

  override def isSatisfiedBy(t: T): Boolean

  override def and(specification: Specification[T]): Specification[T] =
    AndSpecification(this, specification)

  override def or(specification: Specification[T]): Specification[T] =
    OrSpecification(this, specification)

  override def not(specification: Specification[T]): Specification[T] =
    NotSpecification(specification)
}
