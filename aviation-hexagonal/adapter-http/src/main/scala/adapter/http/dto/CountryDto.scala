package adapter.http.dto

import domain.model.Country
import sttp.tapir.Schema
import sttp.tapir.Validator

case class CountryDto(code: String, name: String)

object CountryDto {
  def fromDomain(country: Country): CountryDto =
    CountryDto(code = country.code.value, name = country.name)

  given Schema[CountryDto] = Schema.derived[CountryDto]
    .modify(_.code)(
      _.description("ISO 3166-1 alpha-2 country code.")
        .validate(Validator.minLength(2))
        .validate(Validator.maxLength(2))
    )
    .modify(_.name)(_.description("Full country name."))
}
