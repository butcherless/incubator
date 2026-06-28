package adapter.http.endpoint

import adapter.http.dto.CountryDto
import adapter.http.error.{ErrorMapper, HttpErrorResponse}
import domain.port.in.FindCountryUseCase
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

object CountryEndpoints {

  private val base = endpoint.in("api" / "v1" / "countries")

  val findAll: PublicEndpoint[(Int, Int), (StatusCode, HttpErrorResponse), List[CountryDto], Any] =
    base.get
      .summary("List countries")
      .description("Returns a paginated list of all countries.")
      .tag("Countries")
      .in(query[Int]("page").description("Page number (1-based).").default(1))
      .in(query[Int]("pageSize").description("Number of results per page.").default(20))
      .out(jsonBody[List[CountryDto]].description("List of countries."))
      .errorOut(statusCode.and(jsonBody[HttpErrorResponse].description("An error occurred.")))

  val findByCode: PublicEndpoint[String, (StatusCode, HttpErrorResponse), CountryDto, Any] =
    base.get
      .summary("Find country by code")
      .description("Returns a single country identified by its ISO 3166-1 alpha-2 code.")
      .tag("Countries")
      .in(path[String]("code").description("ISO 3166-1 alpha-2 country code (e.g. ES)."))
      .out(jsonBody[CountryDto].description("The requested country."))
      .errorOut(
        oneOf[(StatusCode, HttpErrorResponse)](
          oneOfVariantValueMatcher(
            StatusCode.NotFound,
            statusCode.and(jsonBody[HttpErrorResponse].description("Country not found."))
          ) { case (s, _) => s == StatusCode.NotFound },
          oneOfDefaultVariant(statusCode.and(jsonBody[HttpErrorResponse].description("Unexpected error.")))
        )
      )

  def routes(useCase: FindCountryUseCase): Routes[Any, Response] =
    ZioHttpInterpreter().toHttp(
      findAll.zServerLogic { input =>
        val (page, pageSize) = input
        useCase
          .findAll(Pagination(page, pageSize))
          .map(_.map(CountryDto.fromDomain))
          .mapError(ErrorMapper.toHttpError)
      }
    ) ++
      ZioHttpInterpreter().toHttp(
        findByCode.zServerLogic { code =>
          useCase
            .findByCode(code)
            .map(CountryDto.fromDomain)
            .mapError(ErrorMapper.toHttpError)
        }
      )
}
