package com.cmartin.learn.web

import akka.http.scaladsl.server.Route
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

trait ActuatorApi {

  val route: Route =
    ActuatorEndpoint.healthEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.buildInfo()))
    }

}

object ActuatorApi extends ActuatorApi
