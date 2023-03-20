package com.cmartin.bluezone.adapter.spy

import com.cmartin.bluezone.hexagon.ForPaying
import com.cmartin.bluezone.hexagon.Model.{ DomainError, PayRequest }
import zio.{ IO, ZIO }

case class SpyPaymentServiceAdapter()
    extends ForPaying {

  val VALID_PAYMENT_CARD = "5200828282828210"

  // TODO ZIO Ref
  private val paymentSpool: scala.collection.mutable.ListBuffer[PayRequest] = scala.collection.mutable.ListBuffer.empty

  override def pay(payRequest: PayRequest): IO[DomainError, Unit] =
    if (payRequest.paymentCard == VALID_PAYMENT_CARD)
      ZIO.succeed(paymentSpool += payRequest)
    else ZIO.fail(DomainError.PaymentError("Invalid card. Payment failed."))

  override def lastPayRequest(): IO[DomainError, PayRequest] =
    paymentSpool.lastOption match {
      case Some(pr) => ZIO.succeed(pr)
      case None     => ZIO.fail(DomainError.PayRequestNotFound("Last pay request not found"))
    }
}
