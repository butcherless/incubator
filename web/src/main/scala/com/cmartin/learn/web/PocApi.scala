package com.cmartin.learn.web
import akka.http.scaladsl.server.Route
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

trait PocApi {
  val resultRoute: Route =
    PocEndpoint.resultEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.Success))
    }

  val bookRoute: Route =
    PocEndpoint.bookEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.bookExample))
    }

}

object PocApi extends PocApi
