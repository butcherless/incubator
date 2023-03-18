package com.cmartin.learn.domain

import com.cmartin.learn.domain.Model.{ Airport, Country }

import scala.concurrent.Future

object ApplicationPorts {

  /** Country Service (Port)
    */
  trait CountryService {

    /** @param country
      * @return
      */
    def create(country: Country): Future[Country]

    /** @param country
      * @return
      */
    def update(country: Country): Future[Country]

    /** @param country
      * @return
      */
    def delete(country: Country): Future[Country]

    /** @param code
      * @return
      */
    def searchByCode(code: String): Future[Country]

    /** @param country
      * @return
      */
    def exists(country: Country): Future[Boolean]
  }

  trait AirportService {
    def create(airport: Airport): Future[Airport]
    def update(airport: Airport): Future[Airport]
    def delete(airport: Airport): Future[Int]
  }

}
