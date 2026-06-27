package domain.model

opaque type CountryCode = String

object CountryCode {
  def apply(value: String): CountryCode        = value
  extension (c: CountryCode) def value: String = c
}

case class Country(code: CountryCode, name: String)
