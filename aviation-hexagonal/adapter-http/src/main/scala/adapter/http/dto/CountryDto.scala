package adapter.http.dto

import domain.model.Country

case class CountryDto(code: String, name: String)

object CountryDto {
  def fromDomain(country: Country): CountryDto =
    CountryDto(code = country.code.value, name = country.name)
}
