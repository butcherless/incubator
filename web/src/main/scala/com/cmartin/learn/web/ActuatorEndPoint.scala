package com.cmartin.learn.web

import com.cmartin.learn.web.ApiModel.{Book, BuildInfo}
import sttp.model.StatusCode
import sttp.tapir.json.upickle._
import sttp.tapir.{Endpoint, _}


// TODO
trait ActuatorEndPoint {

  type HealthInfo = BuildInfo

  val baseEndpointInput: EndpointInput[Unit] = "api" / "v1.0"

  lazy val healthEndpoint: Endpoint[Unit, StatusCode, HealthInfo, Nothing] =
    endpoint
      .get
      .in(baseEndpointInput / "health")
      .name("health-resource")
      .description("Health Check Endpoin")
      .out(jsonBody[HealthInfo].example(ApiModel.buildInfo))
      .errorOut(statusCode)

}

object ActuatorEndPoint extends ActuatorEndPoint