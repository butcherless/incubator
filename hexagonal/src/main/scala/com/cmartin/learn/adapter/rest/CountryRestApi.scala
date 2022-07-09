package com.cmartin.learn.adapter.rest

import com.cmartin.learn.adapter.rest.CountryValidator.RestValidationError
import com.cmartin.learn.domain.ApplicationPorts.CountryService
import com.cmartin.learn.domain.Model.Country
import zio.prelude.Validation

import scala.concurrent.Future

class CountryRestApi(countryService: CountryService) {

  // Dummy API
  def post(name: String, code: String): Future[Country] = {
    val countryValidation: Validation[RestValidationError, Country] =
      CountryValidator.validate(name, code)

    // TODO
    countryValidation.toEither
      .fold[Future[Country]](
        nec => Future.failed(new RuntimeException(s"StatusCode: 500 - ${nec.mkString("[", ",", "]")}")),
        country => countryService.create(country)
      )
  }

}
