package adapter.http.error

import domain.error.DomainError
import domain.error.DomainError.*
import sttp.model.StatusCode

case class ApiError(statusCode: StatusCode, message: String)

case class HttpErrorResponse(message: String)

object ErrorMapper {

  def toApiError(error: DomainError): ApiError = error match {
    case CountryNotFound(code)    => ApiError(StatusCode.NotFound, s"Country not found: $code")
    case AirportNotFound(iata)    => ApiError(StatusCode.NotFound, s"Airport not found: $iata")
    case AirlineNotFound(icao)    => ApiError(StatusCode.NotFound, s"Airline not found: $icao")
    case RouteNotFound(id)        => ApiError(StatusCode.NotFound, s"Route not found: $id")
    case RouteAlreadyExists(o, d) => ApiError(StatusCode.Conflict, s"Route already exists: $o -> $d")
    case InvalidRoute(reason)     => ApiError(StatusCode.BadRequest, reason)
    case DatabaseError(cause)     => ApiError(StatusCode.InternalServerError, s"Database error: $cause")
    case MessagingError(cause)    => ApiError(StatusCode.InternalServerError, s"Messaging error: $cause")
  }

  def toMessage(error: DomainError): String = toApiError(error).message

  def toHttpError(error: DomainError): (StatusCode, HttpErrorResponse) = {
    val e = toApiError(error)
    (e.statusCode, HttpErrorResponse(e.message))
  }
}
