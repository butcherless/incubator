package com.cmartin.learn.specs

/** NOT decorator, used to create a new specification that is the inverse (NOT)
  * of the given spec.
  *
  * @param spec1
  *   Specification instance to not.
  */
class NotSpecification[T](spec1: Specification[T])
    extends AbstractSpecification[T] {

  def isSatisfiedBy(t: T): Boolean =
    !spec1.isSatisfiedBy(t)

}

object NotSpecification {
  def apply[T](spec1: Specification[T]): NotSpecification[T] =
    new NotSpecification(spec1)
}
