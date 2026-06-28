package adapter.http.server

import adapter.http.endpoint.{AirportEndpoints, CountryEndpoints, RouteEndpoints}
import domain.port.in.{CreateRouteUseCase, FindAirportUseCase, FindCountryUseCase}
import sttp.apispec.Tag as ApiTag
import sttp.apispec.openapi.{Contact, Info, License}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio.*
import zio.http.{Response, Routes, Server}

object HttpServer {

  val port: Int = sys.env.get("HTTP_PORT").flatMap(_.toIntOption).getOrElse(8080)

  private val apiInfo = Info(
    title = "Aviation Hexagonal API",
    version = "0.1.0",
    description = Some("REST API for managing countries, airports, airlines, and flight routes."),
    contact = Some(Contact(name = Some("Aviation API Team"), email = Some("api@aviation.example"))),
    license = Some(License(name = "Apache 2.0", url = Some("https://www.apache.org/licenses/LICENSE-2.0")))
  )

  private val topLevelTags = List(
    ApiTag("Countries", description = Some("Country lookup operations.")),
    ApiTag("Airports", description = Some("Airport lookup operations.")),
    ApiTag("Routes", description = Some("Flight route management operations."))
  )

  def allRoutes(
      findCountryUseCase: FindCountryUseCase,
      findAirportUseCase: FindAirportUseCase,
      createRouteUseCase: CreateRouteUseCase
  ): Routes[Any, Response] =
    CountryEndpoints.routes(findCountryUseCase) ++
      AirportEndpoints.routes(findAirportUseCase) ++
      RouteEndpoints.routes(createRouteUseCase) ++
      ZioHttpInterpreter().toHttp(
        SwaggerInterpreter(customiseDocsModel = _.tags(topLevelTags))
          .fromEndpoints[Task](
            List(
              CountryEndpoints.findAll,
              CountryEndpoints.findByCode,
              AirportEndpoints.findAll,
              AirportEndpoints.findByIata,
              RouteEndpoints.create
            ),
            apiInfo
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
