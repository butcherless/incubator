package com.cmartin.learn.adapter.rest

import com.cmartin.learn.domain.CountryService
import com.cmartin.learn.domain.Model.Country
import zio.prelude.Validation

import scala.concurrent.Future


class CountryRestApi(countryCreator: CountryService) {
/*
  // Dummy api, returns http-code only
  def post(name: String, code: String): Future[Country] = {
    val countryValidation: Validation[CountryValidator.RestValidationError, Country] =
      CountryValidator.validate(name, code)

    countryValidation
      .fold(
        _ => Future.failed(new RuntimeException("StatusCode: 500")), // KO
        country => {
          countryCreator.create(country)
          //200 // OK
        }
      )
  }
*/
}
