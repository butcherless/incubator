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
  val registrationMNS = "ec-mns"

  val fleet = TableQuery[Fleet]

  var db: Database = _


  it should "create the fleet database" in {
    val tables = db.run(MTable.getTables).futureValue

    tables.size shouldBe 1
    tables.count(_.name.name == TableNames.fleet) shouldBe 1
  }

  it should "insert an aircraft into the database" in {
    val count = insertAircraft()

    count shouldBe 1
  }

  it should "retrieve an aircraft from the database" in {
    insertAircraft()

    val list = db.run(fleet.filter(_.registration === registrationMIG).result).futureValue

    list.size shouldBe 1
    val aircraft = list.head
    aircraft.id.isDefined shouldBe true
    aircraft.typeCode shouldBe TypeCodes.BOEING_787_800
    aircraft.registration shouldBe registrationMIG
  }

  it should "update an aircraft into the database" in {
    insertAircraft()

    val updateAction = fleet.filter(_.registration === registrationMIG)
      .map(a => a.registration)
      .update(registrationMNS)

    val selectAction = fleet.filter(_.registration === registrationMNS).result

    val list = db.run(updateAction andThen selectAction).futureValue

    list.nonEmpty shouldBe true
    val aircraft = list.head
    aircraft.registration shouldBe registrationMNS
  }

  it should "delete an aircraft from the dataase" in {
    insertAircraft()
    val q1 = fleet.filter(_.registration === registrationMIG)
    val a1 = q1.result
    val a2 = q1.delete
    val a3 = fleet.length.result

    val count = db.run(
      a1 andThen a2 andThen a3
    ).futureValue

    count shouldBe 0
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
