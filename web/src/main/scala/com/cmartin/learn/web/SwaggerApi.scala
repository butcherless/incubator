package com.cmartin.learn.web

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.cmartin.learn.web.CommonEndpoint.{API_TEXT, API_VERSION}
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

trait SwaggerApi {

  lazy val docsAsYaml: String = List(
    ActuatorEndpoint.healthEndpoint,
    PocEndpoint.resultEndpoint,
    PocEndpoint.bookEndpoint
  ).toOpenAPI("Demo Service API", "1.0.0-SNAPSHOT").toYaml

  private lazy val contextPath = "docs"
  private lazy val yamlName    = "docs.yaml"

  lazy val route: Route =
    pathPrefix(API_TEXT / API_VERSION) {
      new SwaggerAkka(docsAsYaml).routes
    }
}

object SwaggerApi extends SwaggerApi
