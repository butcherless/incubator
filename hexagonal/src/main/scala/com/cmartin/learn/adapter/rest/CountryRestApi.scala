package com.cmartin.learn.adapter.rest

import com.cmartin.learn.domain.ApplicationPorts.CountryService
import com.cmartin.learn.domain.Model.Country
import zio.prelude.Validation

import scala.concurrent.Future

import CountryValidator.RestValidationError

class CountryRestApi(countryService: CountryService) {

  // Dummy API
  def post(name: String, code: String): Future[Country] = {
    val countryValidation: Validation[RestValidationError, Country] =
      CountryValidator.validate(name, code)

    //TODO
    countryValidation.sandbox.either.run.fold(
      _ => Future.failed(new RuntimeException("StatusCode: 500")), // KO
      country => countryService.create(country)                    // OK
    )
  }

}
