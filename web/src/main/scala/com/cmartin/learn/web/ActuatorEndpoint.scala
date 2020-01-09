package com.cmartin.learn.web

import com.cmartin.learn.web.ApiModel.BuildInfo
import sttp.model.StatusCode
import sttp.tapir.json.upickle._
import sttp.tapir.{Endpoint, _}

// TODO
trait ActuatorEndpoint {

  type HealthInfo = BuildInfo

  lazy val healthEndpoint: Endpoint[Unit, StatusCode, HealthInfo, Nothing] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "health")
      .name("health-endpoint")
      .description("Health Check Endpoint")
      .out(jsonBody[HealthInfo].example(ApiModel.buildInfo))
      .errorOut(statusCode)

}

object ActuatorEndpoint extends ActuatorEndpoint
