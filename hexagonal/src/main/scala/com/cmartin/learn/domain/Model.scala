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

  case class Airport1(
      name: String,
      iataCode: String,
      icaoCode: String,
      countryId: Long
  )

  /** Country object
    * @param name airport name
    * @param iataCode 3 letter code
    * @param icaoCode 4 letter code
    * @param country country to which the airport belongs
    */
  case class Airport(
      name: String,
      iataCode: String,
      icaoCode: String,
      country: Country
  )

}
