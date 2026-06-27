package adapter.http.endpoint

import adapter.http.dto.{CreateRouteRequest, RouteDto}
import adapter.http.error.ErrorMapper
import domain.port.in.{CreateRouteCommand, CreateRouteUseCase}
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.RichZEndpoint
import io.circe.generic.auto.*
import zio.*
import zio.http.{Response, Routes}

object RouteEndpoints {

  private val base = endpoint.in("api" / "v1" / "routes")

  val create: PublicEndpoint[CreateRouteRequest, (StatusCode, String), RouteDto, Any] =
    base.post
      .in(jsonBody[CreateRouteRequest])
      .out(jsonBody[RouteDto].and(statusCode(StatusCode.Created)))
      .errorOut(statusCode.and(stringBody))

  def routes(useCase: CreateRouteUseCase): Routes[Any, Response] =
    ZioHttpInterpreter().toHttp(
      create.zServerLogic { req =>
        useCase
          .create(CreateRouteCommand(req.originIata, req.destinationIata, req.airlineIcao, req.distanceKm))
          .map(RouteDto.fromDomain)
          .mapError(e => (ErrorMapper.toApiError(e).statusCode, ErrorMapper.toMessage(e)))
      }
    )
}
