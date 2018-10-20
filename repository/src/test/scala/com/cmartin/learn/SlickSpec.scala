package com.cmartin.learn

import com.cmartin.learn.repository.frm.Fleet
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable

class SlickSpec extends FlatSpec with Matchers with BeforeAndAfter with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val fleet = TableQuery[Fleet]

  var db: Database = _

  def createSchema() = db.run((fleet.schema).create).futureValue

  before {
    db = Database.forConfig("h2mem")
  }

  it should "create the fleet database" in {
    createSchema()
    
    val tables = db.run(MTable.getTables).futureValue

    tables.size shouldBe 1
  }
}
