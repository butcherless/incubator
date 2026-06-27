package adapter.http.endpoint

import adapter.http.dto.CountryDto
import adapter.http.error.ErrorMapper
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

  val findAll: PublicEndpoint[(Int, Int), (StatusCode, String), List[CountryDto], Any] =
    base.get
      .in(query[Int]("page").default(1))
      .in(query[Int]("pageSize").default(20))
      .out(jsonBody[List[CountryDto]])
      .errorOut(statusCode.and(stringBody))

  val findByCode: PublicEndpoint[String, (StatusCode, String), CountryDto, Any] =
    base.get
      .in(path[String]("code"))
      .out(jsonBody[CountryDto])
      .errorOut(statusCode.and(stringBody))

  def routes(useCase: FindCountryUseCase): Routes[Any, Response] =
    ZioHttpInterpreter().toHttp(
      findAll.zServerLogic { input =>
        val (page, pageSize) = input
        useCase
          .findAll(Pagination(page, pageSize))
          .map(_.map(CountryDto.fromDomain))
          .mapError(e => (ErrorMapper.toApiError(e).statusCode, ErrorMapper.toMessage(e)))
      }
    ) ++
      ZioHttpInterpreter().toHttp(
        findByCode.zServerLogic { code =>
          useCase
            .findByCode(code)
            .map(CountryDto.fromDomain)
            .mapError(e => (ErrorMapper.toApiError(e).statusCode, ErrorMapper.toMessage(e)))
        }
      )
}
