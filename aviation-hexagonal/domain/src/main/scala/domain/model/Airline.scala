package domain.model

opaque type IcaoCode = String

object IcaoCode {
  def apply(value: String): IcaoCode        = value
  extension (i: IcaoCode) def value: String = i
}

case class Airline(
    icao: IcaoCode,
    name: String,
    countryCode: CountryCode
)
