package com.cmartin.bluezone

import com.cmartin.bluezone.Model.{DomainError, PayRequest}
import zio.IO

trait ForPaying {

  def pay(payRequest: PayRequest): IO[DomainError, Unit] // TODO: IO throws PayErrorException

  def lastPayRequest(): IO[DomainError, PayRequest]
}
