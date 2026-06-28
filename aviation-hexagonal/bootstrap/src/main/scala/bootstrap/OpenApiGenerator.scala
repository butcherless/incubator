package bootstrap

import adapter.http.endpoint.{AirportEndpoints, CountryEndpoints, RouteEndpoints}
import adapter.http.server.HttpServer
import sttp.apispec.Tag as ApiTag
import sttp.apispec.openapi.{Contact, Info, License}
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.apispec.openapi.circe.yaml.*

object OpenApiGenerator:
  def main(args: Array[String]): Unit =
    val info = Info(
      title = "Aviation Hexagonal API",
      version = "0.1.0",
      description = Some("REST API for managing countries, airports, airlines, and flight routes."),
      contact = Some(Contact(name = Some("Aviation API Team"), email = Some("api@aviation.example"))),
      license = Some(License(name = "Apache 2.0", url = Some("https://www.apache.org/licenses/LICENSE-2.0")))
    )
    val tags = List(
      ApiTag("Countries", description = Some("Country lookup operations.")),
      ApiTag("Airports", description = Some("Airport lookup operations.")),
      ApiTag("Routes", description = Some("Flight route management operations."))
    )
    val yaml = OpenAPIDocsInterpreter()
      .toOpenAPI(
        List(
          CountryEndpoints.findAll,
          CountryEndpoints.findByCode,
          AirportEndpoints.findAll,
          AirportEndpoints.findByIata,
          RouteEndpoints.create
        ),
        info
      )
      .tags(tags)
      .toYaml
    println(yaml)
