package com.cmartin.learn.web

import com.cmartin.learn.web.ApiModel.{Book, Result, Success}
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.{Endpoint, _}

trait PocEndpoint {

  lazy val resultEndpoint: Endpoint[Unit, StatusCode, Result, Nothing] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "results")
      .name("result-resource")
      .description("Poc Endpoint for learning and testing - result")
      .out(jsonBody[Result].example(Success))
      .errorOut(statusCode)

  lazy val bookEndpoint: Endpoint[Unit, StatusCode, Book, Nothing] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "books")
      .name("book-resource")
      .description("Poc Endpoint for learning and testing - book")
      .out(jsonBody[Book].example(ApiModel.bookExample))
      .errorOut(statusCode)

}

object PocEndpoint extends PocEndpoint
