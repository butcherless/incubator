package com.cmartin.learn.web

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.DebuggingDirectives
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

trait ActuatorApi {

  import ActuatorApi._

  val route: Route = //DebuggingDirectives.logRequestResult(LOGGER_NAME) {
    ActuatorEndPoint.healthEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.buildInfo))
    }
  //}

}

object ActuatorApi extends ActuatorApi {
  val LOGGER_NAME = "actuator-logger"
}
