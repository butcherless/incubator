package com.cmartin.learn.web
import akka.http.scaladsl.server.Route
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

trait PocApi {
  val route: Route =
    PocEndpoint.pocEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.Success))
    }
}

object PocApi extends PocApi