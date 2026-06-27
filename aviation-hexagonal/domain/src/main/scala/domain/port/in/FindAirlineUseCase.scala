package domain.port.in

import domain.error.DomainError
import domain.model.Airline
import shared.Pagination
import zio.IO

trait FindAirlineUseCase {
  def findByIcao(icao: String): IO[DomainError, Airline]
  def findAll(pagination: Pagination): IO[DomainError, List[Airline]]
}
