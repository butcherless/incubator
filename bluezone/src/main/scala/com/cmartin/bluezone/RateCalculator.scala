package com.cmartin.bluezone

import com.cmartin.bluezone.Model.Rate
import zio.{UIO, ZIO}

import java.time.LocalDateTime

final case class RateCalculator(rate: Rate) {
  def getUntilGivenAmount(from: LocalDateTime, amount: BigDecimal): UIO[LocalDateTime] =
    ZIO.succeed {
      val minutes = amount.doubleValue * 60 / rate.amountPerHour.doubleValue
      from.plusMinutes(minutes.longValue())
    }
}
