package com.cmartin.learn.streams

import zio._
import zio.Console._
import zio.Clock._
import com.typesafe.scalalogging.CanLog

object PocStreamApp
    extends zio.ZIOAppDefault {

  import PrimeLib._

  object PrimeLib {
    def calculatePrimesStream(end: Int): LazyList[Int] = {
      if (end < 2) LazyList.empty
      else if (end == 2) LazyList(2)
      else {
        val odds       = LazyList.from(3, 2).takeWhile(_ <= Math.sqrt(end).toInt)
        val composites = odds.flatMap(i => LazyList.from(i * i, 2 * i).takeWhile(_ <= end))
        LazyList(2) ++ LazyList.from(3, 2).takeWhile(_ <= end).diff(composites)
      }
    }

    def calculatePrimesList(end: Int): List[Int] =
      calculatePrimesStream(end).toList.take(200)

    def isPrime(number: Int): UIO[Boolean] = {
      if (number < 2) ZIO.succeed(false)
      else if (number == 2) ZIO.succeed(true)
      else ZIO.succeed(calculatePrimesStream(number).last == number)
    }

    def calcElapsedTime(start: Long, stop: Long) =
      ZIO.succeed((stop - start) / Math.pow(10, 6).toLong)
  }

  val number: Int = 3

  def run =
    for {
      startTime   <- Clock.nanoTime
      primes      <- ZIO.succeed(calculatePrimesList(number))
      stopTime    <- Clock.nanoTime
      elapsedTime <- calcElapsedTime(startTime, stopTime)
      _           <- printLine(s"primes until $number are: $primes in $elapsedTime milliseconds")
      result      <- isPrime(number)
      _           <- printLine(s"is prime $number: $result")
    } yield ()
}
