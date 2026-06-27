package application.service

import domain.error.DomainError
import domain.error.DomainError.AirportNotFound
import domain.model.{IataCode, IcaoCode, Route, RouteId}
import domain.port.in.{CreateRouteCommand, CreateRouteUseCase}
import domain.port.out.{AirportRepository, RouteRepository}
import domain.service.RouteValidator
import zio.*

final class CreateRouteService(
    airportRepository: AirportRepository,
    routeRepository: RouteRepository
) extends CreateRouteUseCase {

  override def create(command: CreateRouteCommand): IO[DomainError, Route] =
    for {
      origin      <- resolveAirport(command.originIata)
      destination <- resolveAirport(command.destinationIata)
      _           <- RouteValidator.validate(origin.iata, destination.iata, command.distanceKm)
      route        = Route(
                       id = RouteId.generate,
                       origin = origin.iata,
                       destination = destination.iata,
                       airlineIcao = IcaoCode(command.airlineIcao),
                       distanceKm = command.distanceKm
                     )
      saved       <- routeRepository.save(route)
    } yield saved

  private def resolveAirport(iata: String) =
    airportRepository.findByIata(IataCode(iata)).flatMap {
      case Some(a) => ZIO.succeed(a)
      case None    => ZIO.fail(AirportNotFound(iata))
    }
}

object CreateRouteService {
  val layer: URLayer[AirportRepository & RouteRepository, CreateRouteUseCase] =
    ZLayer.fromFunction(new CreateRouteService(_, _))
}
