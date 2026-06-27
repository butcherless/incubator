package adapter.http.server

import adapter.http.endpoint.{AirportEndpoints, CountryEndpoints, RouteEndpoints}
import domain.port.in.{CreateRouteUseCase, FindAirportUseCase, FindCountryUseCase}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio.*
import zio.http.{Response, Routes, Server}

object HttpServer {

  val port: Int = sys.env.get("HTTP_PORT").flatMap(_.toIntOption).getOrElse(8080)

  def allRoutes(
      findCountryUseCase: FindCountryUseCase,
      findAirportUseCase: FindAirportUseCase,
      createRouteUseCase: CreateRouteUseCase
  ): Routes[Any, Response] =
    CountryEndpoints.routes(findCountryUseCase) ++
      AirportEndpoints.routes(findAirportUseCase) ++
      RouteEndpoints.routes(createRouteUseCase) ++
      ZioHttpInterpreter().toHttp(
        SwaggerInterpreter().fromEndpoints[Task](
          List(
            CountryEndpoints.findAll,
            CountryEndpoints.findByCode,
            AirportEndpoints.findAll,
            AirportEndpoints.findByIata,
            RouteEndpoints.create
          ),
          title = "Aviation Hexagonal API",
          version = "0.1.0"
        )
      )

  val serve: ZIO[FindCountryUseCase & FindAirportUseCase & CreateRouteUseCase, Throwable, Nothing] =
    for {
      findCountry <- ZIO.service[FindCountryUseCase]
      findAirport <- ZIO.service[FindAirportUseCase]
      createRoute <- ZIO.service[CreateRouteUseCase]
      _           <- ZIO.logInfo(s"HTTP server starting on port $port")
      result      <- Server.serve(allRoutes(findCountry, findAirport, createRoute).handleError(identity))
                       .provide(Server.defaultWithPort(port))
    } yield result
}
