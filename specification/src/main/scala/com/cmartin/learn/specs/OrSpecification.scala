package com.cmartin.learn.specs

/** OR specification, used to create a new specification that is the OR of two
  * other specifications.
  * @param spec1
  *   Specification one.
  * @param spec2
  *   Specification two.
  */
class OrSpecification[T](spec1: Specification[T], spec2: Specification[T])
    extends AbstractSpecification[T] {

  def isSatisfiedBy(t: T): Boolean =
    spec1.isSatisfiedBy(t) || spec2.isSatisfiedBy(t)

}

object OrSpecification {
  def apply[T](spec1: Specification[T], spec2: Specification[T]): OrSpecification[T] =
    new OrSpecification(spec1, spec2)
}
