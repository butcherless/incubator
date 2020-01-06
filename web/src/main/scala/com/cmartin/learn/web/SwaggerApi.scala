package com.cmartin.learn.web

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._

trait SwaggerApi {

  lazy val openApi: String = Seq(
    ActuatorEndPoint.healthEndpoint
  ).toOpenAPI("Demo Service API", "1.0.0-SNAPSHOT")
    .toYaml

  private lazy val contextPath = "docs"
  private lazy val yamlName = "docs.yaml"

  lazy val route: Route =
    pathPrefix("api" / "v1.0") {
      pathPrefix(contextPath) {
        pathEndOrSingleSlash {
          redirect(s"$contextPath/index.html?url=/api/v1.0/$contextPath/$yamlName", StatusCodes.PermanentRedirect)
        } ~ path(yamlName) {
          complete(openApi)
        } ~ getFromResourceDirectory("META-INF/resources/webjars/swagger-ui/3.24.3/")
      }
    }

}

object SwaggerApi extends SwaggerApi
