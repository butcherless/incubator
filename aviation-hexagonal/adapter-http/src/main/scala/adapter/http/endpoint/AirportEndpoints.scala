package adapter.http.endpoint

import adapter.http.dto.AirportDto
import adapter.http.error.{ErrorMapper, HttpErrorResponse}
import domain.port.in.FindAirportUseCase
import shared.Pagination
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.RichZEndpoint
import io.circe.generic.auto.*
import zio.*
import zio.http.{Response, Routes}

object AirportEndpoints {

  private val base = endpoint.in("api" / "v1" / "airports")

  val findAll: PublicEndpoint[(Int, Int), (StatusCode, HttpErrorResponse), List[AirportDto], Any] =
    base.get
      .summary("List airports")
      .description("Returns a paginated list of all airports.")
      .tag("Airports")
      .in(query[Int]("page").description("Page number (1-based).").default(1))
      .in(query[Int]("pageSize").description("Number of results per page.").default(20))
      .out(jsonBody[List[AirportDto]].description("List of airports."))
      .errorOut(statusCode.and(jsonBody[HttpErrorResponse].description("An error occurred.")))

  val findByIata: PublicEndpoint[String, (StatusCode, HttpErrorResponse), AirportDto, Any] =
    base.get
      .summary("Find airport by IATA code")
      .description("Returns a single airport identified by its 3-letter IATA code.")
      .tag("Airports")
      .in(path[String]("iata").description("3-letter IATA airport code (e.g. MAD)."))
      .out(jsonBody[AirportDto].description("The requested airport."))
      .errorOut(
        oneOf[(StatusCode, HttpErrorResponse)](
          oneOfVariantValueMatcher(
            StatusCode.NotFound,
            statusCode.and(jsonBody[HttpErrorResponse].description("Airport not found."))
          ) { case (s, _) => s == StatusCode.NotFound },
          oneOfDefaultVariant(statusCode.and(jsonBody[HttpErrorResponse].description("Unexpected error.")))
        )
      )

  def routes(useCase: FindAirportUseCase): Routes[Any, Response] =
    ZioHttpInterpreter().toHttp(
      findAll.zServerLogic { input =>
        val (page, pageSize) = input
        useCase
          .findAll(Pagination(page, pageSize))
          .map(_.map(AirportDto.fromDomain))
          .mapError(ErrorMapper.toHttpError)
      }
    ) ++
      ZioHttpInterpreter().toHttp(
        findByIata.zServerLogic { iata =>
          useCase
            .findByIata(iata)
            .map(AirportDto.fromDomain)
            .mapError(ErrorMapper.toHttpError)
        }
      )
}
