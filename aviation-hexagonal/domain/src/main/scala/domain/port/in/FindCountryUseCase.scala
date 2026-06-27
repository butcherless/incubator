package domain.port.in

import domain.error.DomainError
import domain.model.Country
import shared.Pagination
import zio.IO

trait FindCountryUseCase {
  def findByCode(code: String): IO[DomainError, Country]
  def findAll(pagination: Pagination): IO[DomainError, List[Country]]
}
