package com.cmartin.learn.repository

import com.cmartin.learn.test.Constants._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.duration._

class DummySpec extends AsyncFlatSpec with Matchers {

  behavior of "DummySpec"

  it should "pass" in {
    assert(false)
  }
}
