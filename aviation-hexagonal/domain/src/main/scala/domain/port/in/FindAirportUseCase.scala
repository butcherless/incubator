package domain.port.in

import domain.error.DomainError
import domain.model.Airport
import shared.Pagination
import zio.IO

trait FindAirportUseCase {
  def findByIata(iata: String): IO[DomainError, Airport]
  def findAll(pagination: Pagination): IO[DomainError, List[Airport]]
}
