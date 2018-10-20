package com.cmartin.learn

import com.cmartin.learn.repository.frm.{Aircraft, Fleet, TableNames, TypeCodes}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable

class SlickSpec extends FlatSpec with Matchers with BeforeAndAfter with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val registrationMIG = "ec-mig"

  val fleet = TableQuery[Fleet]

  var db: Database = _


  it should "create the fleet database" in {
    val tables = db.run(MTable.getTables).futureValue

    tables.size shouldBe 1
    tables.count(_.name.name == TableNames.fleet) shouldBe 1
  }

  it should "insert an aircraft" in {
    val count = insertAircraft()

    count shouldBe 1
  }

  it should "retrieve an aircraft" in {
    insertAircraft()

    val list = db.run(fleet.filter(_.registration === registrationMIG).result).futureValue

    list.size shouldBe 1
    list.head.registration shouldBe registrationMIG
  }


  /*
   _    _   ______   _        _____    ______   _____     _____
  | |  | | |  ____| | |      |  __ \  |  ____| |  __ \   / ____|
  | |__| | | |__    | |      | |__) | | |__    | |__) | | (___
  |  __  | |  __|   | |      |  ___/  |  __|   |  _  /   \___ \
  | |  | | | |____  | |____  | |      | |____  | | \ \   ____) |
  |_|  |_| |______| |______| |_|      |______| |_|  \_\ |_____/
  */

  def insertAircraft(): Int =
    db.run(fleet += Aircraft(None, TypeCodes.BOEING_787_800, registrationMIG)).futureValue

  def createSchema() = db.run((fleet.schema).create).futureValue

  before {
    db = Database.forConfig("h2mem")
    createSchema()
  }

  after {
    db.close
  }

}
