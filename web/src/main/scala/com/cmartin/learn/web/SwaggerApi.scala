package com.cmartin.learn.web

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.cmartin.learn.web.CommonEndpoint.{API_TEXT, API_VERSION}
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._

trait SwaggerApi {

  lazy val openApi: String = Seq(
    ActuatorEndpoint.healthEndpoint,
    PocEndpoint.resultEndpoint,
    PocEndpoint.bookEndpoint
  ).toOpenAPI("Demo Service API", "1.0.0-SNAPSHOT").toYaml

  private lazy val contextPath = "docs"
  private lazy val yamlName    = "docs.yaml"

  private lazy val redirectUrl =
    s"$contextPath/index.html?url=/$API_TEXT/$API_VERSION/$contextPath/$yamlName"

  private lazy val resourceDirectory = "META-INF/resources/webjars/swagger-ui/3.24.3/"

  lazy val route: Route =
    pathPrefix(API_TEXT / API_VERSION) {
      pathPrefix(contextPath) {
        pathEndOrSingleSlash {
          redirect(
            redirectUrl,
            StatusCodes.PermanentRedirect
          )
        } ~ path(yamlName) {
          complete(openApi)
        } ~ getFromResourceDirectory(resourceDirectory)
      }
    }

}

object SwaggerApi extends SwaggerApi
