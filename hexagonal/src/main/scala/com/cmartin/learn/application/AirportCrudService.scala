package com.cmartin.learn.application

import com.cmartin.learn.adapter.postgres.Model.AirportDboConverter
import com.cmartin.learn.adapter.postgres.SlickRepositories.DAL
import com.cmartin.learn.domain.ApplicationPorts.AirportService
import com.cmartin.learn.domain.Model.Airport

import scala.concurrent.Future

/* dbo.id.get: Safe access to the identifier is guaranteed by the Repository primary key
 */
class AirportCrudService(dal: DAL) extends AirportService {

  import dal._

  override def create(airport: Airport): Future[Airport] = {
    for {
      country <- findCountryByCode(airport.country.code)
      _       <- airportRepo.insert(AirportDboConverter.from(airport, country.id.get))
    } yield airport
  }

  override def update(airport: Airport): Future[Airport] = {
    for {
      airportDbo <- findAirportByIataCode(airport.iataCode)
      country    <- findCountryByCode(airport.country.code)
      _          <- airportRepo.update(AirportDboConverter.update(airportDbo, airport, country.id.get))
    } yield airport
  }

  override def delete(airport: Airport): Future[Int] = {
    for {
      airportDbo <- findAirportByIataCode(airport.iataCode)
      count      <- airportRepo.delete(airportDbo.id.get)
    } yield count
  }

  /* H E L P E R   F U N C T I O N S */
  // TODO move to common use cases class
  private def findCountryByCode(code: String) = {
    for {
      countryOption <- countryRepo.findByCode(code)
      country       <- checkElement(countryOption, s"country code not found: $code")
    } yield country
  }

  private def findAirportByIataCode(code: String) = {
    for {
      airportOption <- airportRepo.findByIataCode(code)
      airport       <- checkElement(airportOption, s"airport iata code not found: $code")
    } yield airport
  }
}
