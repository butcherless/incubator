package com.cmartin.bluezone

import Model.{PurchaseTicketRequest, Rate, Ticket}

trait ForParkingCars {

  /** Returns the available rates in the city, indexed by name.
    *
    * @return
    *   a map of Rate objects, with the rate name as the key.
    * @see
    *   Rate
    */
  def getAllRatesByName(): Map[String, Rate]

  /** It pays for a parking ticket, which will be valid for the following period
    * of time:
    *   - Starting: Current date-time.
    *   - Ending: Date-time calculated from the payment amount, according to the
    *     rate of the zone the car is parked at. The payment is done by charging
    *     the amount to the wallet associated to the car.
    *
    * @param purchaseTicketRequest
    *   Data needed for purchasing a parking ticket.
    * @return
    *   The code of the purchased ticket, useful for retrieving the whole ticket
    *   data later on.
    * @throws PayErrorException
    *   When the amount in the wallet associated to the car is lower than the
    *   amount to pay for the ticket.
    * @see
    *   PurchaseTicketRequest
    * @see
    *   Ticket
    * @see
    *   PayErrorException
    */
  def purchaseTicket(purchaseTicketRequest: PurchaseTicketRequest): String // IO throws PayErrorException

  /** Given the code of a previously purchased ticket, returns the whole data of
    * the ticket.
    *
    * @param ticketCode
    *   Code of a purchased ticket.
    * @return
    *   The ticket with the given ticket code, or null if it doesn't exist any
    *   ticket with such a code.
    */
  def getTicket(ticketCode: String): Ticket
}
