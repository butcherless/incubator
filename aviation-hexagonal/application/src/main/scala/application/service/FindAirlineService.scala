package application.service

import domain.error.DomainError
import domain.error.DomainError.AirlineNotFound
import domain.model.{Airline, IcaoCode}
import domain.port.in.FindAirlineUseCase
import domain.port.out.AirlineRepository
import shared.Pagination
import zio.{IO, ZIO, URLayer, ZLayer}

final class FindAirlineService(repo: AirlineRepository) extends FindAirlineUseCase {

  override def findByIcao(icao: String): IO[DomainError, Airline] =
    repo.findByIcao(IcaoCode(icao)).flatMap {
      case Some(airline) => ZIO.succeed(airline)
      case None          => ZIO.fail(AirlineNotFound(icao))
    }

  override def findAll(pagination: Pagination): IO[DomainError, List[Airline]] =
    repo.findAll(pagination)
}

object FindAirlineService {
  val layer: URLayer[AirlineRepository, FindAirlineUseCase] =
    ZLayer.fromFunction(new FindAirlineService(_))
}
