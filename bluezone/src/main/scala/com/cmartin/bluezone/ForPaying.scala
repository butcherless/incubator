package com.cmartin.bluezone

import Model.PayRequest

trait ForPaying {

  def pay(payRequest: PayRequest): Unit // TODO: IO throws PayErrorException

  def lastPayRequest(): PayRequest
}
