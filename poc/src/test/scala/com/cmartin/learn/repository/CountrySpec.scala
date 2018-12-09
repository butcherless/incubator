package com.cmartin.learn.repository

import com.cmartin.learn.repository.slick3.{Countries, Country, CountryRepository, TableNames}
import com.cmartin.learn.test.Constants
import org.scalatest.OptionValues._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.Await

class CountrySpec extends FlatSpec with Matchers with BeforeAndAfterEach with ScalaFutures {
  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  implicit var db: Database = _

  it should "insert a country into the database" in {
    val countryRepo = new CountryRepository
    val count = countryRepo.insert(Country(esCountry._1, esCountry._2))

    count.futureValue shouldBe 1
  }


  it should "find Country table name" in {
    val tables = db.run(MTable.getTables).futureValue

    tables.size shouldBe 1

    val table = tables.head
    table.name.name shouldBe TableNames.countries
    table.name.catalog.value shouldBe "AVIATIONPOC"

    val debugMessage = s"-------> result: $table"

    println(s"\n$debugMessage\n")
  }


  val schemaActionList = List(
    TableQuery[Countries]
  )

  def createSchema() = {
    db.run(DBIO.sequence(schemaActionList.map(_.schema.create)))
  }

  override def beforeEach() = {
    db = Database.forConfig("h2mem")
    Await.result(createSchema(), Constants.waitTimeout)
  }

  override def afterEach() = {
    db.close
  }

  /*
    _____         _     ____        _
   |_   _|__  ___| |_  |  _ \  __ _| |_ __ _
     | |/ _ \/ __| __| | | | |/ _` | __/ _` |
     | |  __/\__ \ |_  | |_| | (_| | || (_| |
     |_|\___||___/\__| |____/ \__,_|\__\__,_|

   */

  val esCountry = ("Spain", "ES")

}
