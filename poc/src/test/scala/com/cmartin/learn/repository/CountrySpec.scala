package com.cmartin.learn.repository

import com.cmartin.learn.repository.slick3.{Countries, Country, CountryRepository}
import org.scalatest.OptionValues._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

class CountrySpec extends EntitySpec {

  val tableList = List(
    TableQuery[Countries]
  )

  it should "insert a country into the database" in new Repos {
    val count = countryRepo.insert(Country(esCountry._1, esCountry._2))

    count.futureValue shouldBe 1
  }

  it should "find Country table name" in {
    val tables = db.run(MTable.getTables).futureValue

    tables.size shouldBe 1

    val table = tables.head
    table.name.catalog.value shouldBe "AVIATIONPOC"

    val debugMessage = s"-------> result: $table"

    println(s"\n$debugMessage\n")
  }

  trait Repos {
    val countryRepo = new CountryRepository
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
