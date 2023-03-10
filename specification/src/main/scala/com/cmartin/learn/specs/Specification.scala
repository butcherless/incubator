package com.cmartin.learn.specs

/** Specification interface. <p/> Use AbstractSpecification as base for creating
  * specifications, and only the method isSatisfiedBy( t ) must be implemented.
  */
trait Specification[T] {

  /** Check if t is satisfied by the specification.
    *
    * @param t
    *   Object to test.
    * @return
    *   true if t satisfies the specification.
    */
  def isSatisfiedBy(t: T): Boolean

  /** Create a new specification that is the AND operation of this specification
    * and another specification.
    *
    * @param specification
    *   Specification to AND.
    * @return
    *   A new specification.
    */
  def and(specification: Specification[T]): Specification[T]

  /** Create a new specification that is the OR operation of this specification
    * and another specification.
    *
    * @param specification
    *   Specification to OR.
    * @return
    *   A new specification.
    */
  def or(specification: Specification[T]): Specification[T]

  /** Create a new specification that is the NOT operation of this
    * specification.
    *
    * @param specification
    *   Specification to NOT.
    * @return
    *   A new specification.
    */
  def not(specification: Specification[T]): Specification[T]
}
