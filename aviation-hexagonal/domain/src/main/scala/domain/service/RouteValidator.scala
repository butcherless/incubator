package domain.service

import domain.error.DomainError
import domain.error.DomainError.InvalidRoute
import domain.model.IataCode
import zio.{IO, ZIO}

object RouteValidator {

  def validate(origin: IataCode, destination: IataCode, distanceKm: Int): IO[DomainError, Unit] =
    for {
      _ <- ZIO.fail(InvalidRoute("Origin and destination cannot be the same airport"))
             .when(origin == destination)
      _ <- ZIO.fail(InvalidRoute(s"Distance must be positive, got $distanceKm"))
             .when(distanceKm <= 0)
    } yield ()
}
