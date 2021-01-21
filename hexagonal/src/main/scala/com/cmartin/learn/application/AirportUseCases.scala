package com.cmartin.learn.application

import com.cmartin.learn.adapter.postgres.Model.AirportDboConverter
import com.cmartin.learn.adapter.postgres.SlickRepositories.DAL
import com.cmartin.learn.domain.ApplicationPorts.AirportService
import com.cmartin.learn.domain.Model.Airport

import scala.concurrent.Future

/* dbo.id.get: Access to the identifier is guaranteed by the Repository primary key
 */
class AirportUseCases(dal: DAL) extends AirportService {

  import dal._

  override def create(airport: Airport): Future[Airport] = {
    for {
      countryOption <- dal.countryRepo.findByCode(airport.country.code)
      country       <- checkElement(countryOption, s"country code not found: ${airport.country.code}")
      _             <- dal.airportRepo.insert(AirportDboConverter.from(airport, country.id.get))
    } yield airport
  }

  override def update(airport: Airport): Future[Airport] = {
    for {
      airportOption <- dal.airportRepo.findByIataCode(airport.iataCode)
      airportDbo    <- checkElement(airportOption, s"airport iata code not found: ${airport.iataCode}")
      countryOption <- dal.countryRepo.findByCode(airport.country.code)
      country       <- checkElement(countryOption, s"country code not found: ${airport.country.code}")
      _             <- dal.airportRepo.update(AirportDboConverter.update(airportDbo, airport, country.id.get))
    } yield airport
  }
}
