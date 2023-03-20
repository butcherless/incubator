package com.cmartin.bluezone.adapter.stub

import com.cmartin.bluezone.hexagon.ForObtainingRates
import com.cmartin.bluezone.hexagon.Model.{ DomainError, Rate }
import zio.{ IO, ZIO }

case class StubRateProviderAdapter()
    extends ForObtainingRates {

  // TODO ZIO Ref
  private val rates: scala.collection.mutable.Set[Rate] = scala.collection.mutable.Set.empty

  override def findAll(): IO[DomainError, Set[Rate]] =
    ZIO.succeed(Set.empty[Rate] ++ rates)

  override def findByName(rateName: String): IO[DomainError, Rate] =
    rates.filter(_.name == rateName).size match {
      case 0 => ZIO.fail(DomainError.RateNotFound(s"rate not found: $rateName"))
      case 1 => ZIO.succeed(rates.head)
      case _ => ZIO.fail(DomainError.MultipleRateFound(s"multiple rate found: $rateName"))
    }

  override def addRate(rate: Rate): IO[DomainError, Unit] =
    ZIO.ifZIO(exists(rate.name))(
      ZIO.fail(DomainError.DuplicateRate(rate.name)),
      ZIO.succeed(rates += rate)
    )

  override def exists(rateName: String): IO[DomainError, Boolean] =
    ZIO.succeed(rates.exists(_.name == rateName))

  override def empty(): IO[DomainError, Unit] =
    ZIO.succeed(rates.clear())
}
