package com.cmartin.bluezone.hexagon

import com.cmartin.bluezone.hexagon.Model.{DomainError, PayRequest}
import zio.IO

trait ForPaying {

  def pay(payRequest: PayRequest): IO[DomainError, Unit] // TODO: IO throws PayErrorException

  def lastPayRequest(): IO[DomainError, PayRequest]
}
