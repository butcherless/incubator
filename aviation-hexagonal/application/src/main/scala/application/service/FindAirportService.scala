package application.service

import domain.error.DomainError
import domain.error.DomainError.AirportNotFound
import domain.model.{Airport, IataCode}
import domain.port.in.FindAirportUseCase
import domain.port.out.AirportRepository
import shared.Pagination
import zio.{IO, ZIO, URLayer, ZLayer}

final class FindAirportService(repo: AirportRepository) extends FindAirportUseCase {

  override def findByIata(iata: String): IO[DomainError, Airport] =
    repo.findByIata(IataCode(iata)).flatMap {
      case Some(airport) => ZIO.succeed(airport)
      case None          => ZIO.fail(AirportNotFound(iata))
    }

  override def findAll(pagination: Pagination): IO[DomainError, List[Airport]] =
    repo.findAll(pagination)
}

object FindAirportService {
  val layer: URLayer[AirportRepository, FindAirportUseCase] =
    ZLayer.fromFunction(new FindAirportService(_))
}
