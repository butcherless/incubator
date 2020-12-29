package com.cmartin.learn.domain

object Model {

  /** Country object
    * @param name country name
    * @param code business identifier
    */
  case class Country(
      name: String,
      code: String
  )

  case class Airport(
      name: String,
      iataCode: String,
      icaoCode: String,
      countryId: Long
  )
}
