package application.service

import domain.error.DomainError
import domain.error.DomainError.CountryNotFound
import domain.model.{Country, CountryCode}
import domain.port.in.FindCountryUseCase
import domain.port.out.CountryRepository
import shared.Pagination
import zio.*

final class FindCountryService(repo: CountryRepository) extends FindCountryUseCase {

  override def findByCode(code: String): IO[DomainError, Country] =
    repo.findByCode(CountryCode(code)).flatMap {
      case Some(country) => ZIO.succeed(country)
      case None          => ZIO.fail(CountryNotFound(code))
    }

  override def findAll(pagination: Pagination): IO[DomainError, List[Country]] =
    repo.findAll(pagination)
}

object FindCountryService {
  val layer: URLayer[CountryRepository, FindCountryUseCase] =
    ZLayer.fromFunction(new FindCountryService(_))
}
