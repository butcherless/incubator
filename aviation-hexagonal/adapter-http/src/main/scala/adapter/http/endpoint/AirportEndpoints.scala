package adapter.http.endpoint

import adapter.http.dto.AirportDto
import adapter.http.error.ErrorMapper
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

  val findAll: PublicEndpoint[(Int, Int), (StatusCode, String), List[AirportDto], Any] =
    base.get
      .in(query[Int]("page").default(1))
      .in(query[Int]("pageSize").default(20))
      .out(jsonBody[List[AirportDto]])
      .errorOut(statusCode.and(stringBody))

  val findByIata: PublicEndpoint[String, (StatusCode, String), AirportDto, Any] =
    base.get
      .in(path[String]("iata"))
      .out(jsonBody[AirportDto])
      .errorOut(statusCode.and(stringBody))

  def routes(useCase: FindAirportUseCase): Routes[Any, Response] =
    ZioHttpInterpreter().toHttp(
      findAll.zServerLogic { input =>
        val (page, pageSize) = input
        useCase
          .findAll(Pagination(page, pageSize))
          .map(_.map(AirportDto.fromDomain))
          .mapError(e => (ErrorMapper.toApiError(e).statusCode, ErrorMapper.toMessage(e)))
      }
    ) ++
      ZioHttpInterpreter().toHttp(
        findByIata.zServerLogic { iata =>
          useCase
            .findByIata(iata)
            .map(AirportDto.fromDomain)
            .mapError(e => (ErrorMapper.toApiError(e).statusCode, ErrorMapper.toMessage(e)))
        }
      )
}
