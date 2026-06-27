package domain.error

sealed trait DomainError

object DomainError {
  case class CountryNotFound(code: String)                           extends DomainError
  case class AirportNotFound(iata: String)                           extends DomainError
  case class AirlineNotFound(icao: String)                           extends DomainError
  case class RouteNotFound(id: String)                               extends DomainError
  case class RouteAlreadyExists(origin: String, destination: String) extends DomainError
  case class InvalidRoute(reason: String)                            extends DomainError
  case class DatabaseError(cause: String)                            extends DomainError
  case class MessagingError(cause: String)                           extends DomainError
}
