package com.cmartin.bluezone

import Model.Rate
import java.time.LocalDateTime

final case class RateCalculator(rate: Rate) {
  def getUntilGivenAmount(from: LocalDateTime, amount: BigDecimal) = {
    val minutes = amount.doubleValue * 60 / rate.amountPerHour.doubleValue
    from.plusMinutes(minutes.longValue())
  }
}
