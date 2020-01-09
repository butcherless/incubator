package com.cmartin.learn.web

import com.cmartin.learn.web.ApiModel.{Result, Success}
import sttp.model.StatusCode
import sttp.tapir.json.upickle._
import sttp.tapir.{Endpoint, _}

trait PocEndpoint {

  lazy val pocEndpoint: Endpoint[Unit, StatusCode, Result, Nothing] =
    endpoint.get
      .in(ActuatorEndpoint.baseEndpointInput / "result")
      .name("poc-resources")
      .description("Poc Endpoint for learning and testing")
      .out(jsonBody[Result].example(Success))
      .errorOut(statusCode)
}

object PocEndpoint extends PocEndpoint
