package com.cmartin.learn

import java.text.Normalizer

object VarianceLearning {
  abstract class Vehicle

  case class Car()  extends Vehicle
  case class Bike() extends Vehicle

  // Covariance (contain/produce)
  val vehicleIdentity = (vehicle: Vehicle) => vehicle

  val c1: Vehicle = vehicleIdentity(Car())

  val b1: Vehicle = vehicleIdentity(Bike())

  case class Parking[+A](value: A)

  val carParking: Parking[Vehicle] = Parking[Car](Car())

  // Contravariance (act/consume)
  trait Garage[-A] {
    def repair(a: A): Boolean
  }

  val vehicleGarage = new Garage[Vehicle] {

    override def repair(a: Vehicle): Boolean = a match {
      case Bike() => true
      case Car()  => true
    }

  }

  val car1: Car   = ???
  val bike1: Bike = ???

  val g1: Garage[Car]  = vehicleGarage
  val g2: Garage[Bike] = vehicleGarage

  val result1 = g1.repair(car1)
  val result2 = g2.repair(bike1)

  object PalindromeManager {

    implicit class StringOps(s: String) {
      def isPalindrome: Boolean = isPalindrome_((s))
    }

    /* steps:
       - remove spaces
       - remove accents and diacritics
       - lowercase
     */
    private def isPalindrome_(text: String): Boolean = {
      val blanksRemoved         = text.replaceAll("\\p{Space}+", "")
      val asciiUpercasedRemoved = Normalizer.normalize(blanksRemoved, Normalizer.Form.NFD)
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
        .toUpperCase

      asciiUpercasedRemoved == asciiUpercasedRemoved.reverse
    }

    def findDuplicateWords(w1: String, w2: String): Seq[String] = {
      val x1 = w1.split(' ').toIndexedSeq
      val x2 = w2.split(' ').toIndexedSeq

      x1.intersect(x2).distinct
    }

    def reverseWords(s: String): Seq[String] = {
      s.split(' ')
        .toIndexedSeq
        .reverse
    }

  }

}
