package com.cmartin.learn.specs

/** AND specification, used to create a new specification that is the AND of two
  * other specifications.
  */
class AndSpecification[T](spec1: Specification[T], spec2: Specification[T])
    extends AbstractSpecification[T] {

  def isSatisfiedBy(t: T): Boolean =
    spec1.isSatisfiedBy(t) && spec2.isSatisfiedBy(t)

}

object AndSpecification {
  def apply[T](spec1: Specification[T], spec2: Specification[T]): AndSpecification[T] =
    new AndSpecification(spec1, spec2)
}
