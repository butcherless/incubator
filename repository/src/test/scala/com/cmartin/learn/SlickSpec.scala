package com.cmartin.learn

import com.cmartin.learn.repository.frm._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable

class SlickSpec extends FlatSpec with Matchers with BeforeAndAfter with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val registrationMIG = "ec-mig"
  val registrationMNS = "ec-mns"
  val barajasIataCode = "MAD"

  val ecMigAircraft = Aircraft(None, TypeCodes.BOEING_787_800, registrationMIG)
  val spainCountry = newCountry("Spain")

  val fleet = TableQuery[Fleet]
  val countries = TableQuery[Countries]

  var db: Database = _


  it should "create the aviation database" in {
    val tables = db.run(MTable.getTables).futureValue

    tables.size shouldBe 4
    tables.count(_.name.name == TableNames.fleet) shouldBe 1
    tables.count(_.name.name == TableNames.countries) shouldBe 1
    tables.count(_.name.name == TableNames.airlines) shouldBe 1
    tables.count(_.name.name == TableNames.airports) shouldBe 1
  }

  it should "insert an aircraft into the database" in {
    val query = fleet += ecMigAircraft
    val action = db.run(query)
    val count = action.futureValue

    count shouldBe 1
  }

  it should "retrieve an aircraft from the database" in {
    insertAircraft(ecMigAircraft)

    val list = db.run(fleet.filter(_.registration === registrationMIG).result).futureValue

    list.size shouldBe 1
    val aircraft = list.head
    aircraft.id.isDefined shouldBe true
    aircraft.typeCode shouldBe TypeCodes.BOEING_787_800
    aircraft.registration shouldBe registrationMIG
  }

  it should "update an aircraft into the database" in {
    insertAircraft(ecMigAircraft)

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
    insertAircraft(ecMigAircraft)
    val q1 = fleet.filter(_.registration === registrationMIG)
    val a1 = q1.result
    val a2 = q1.delete
    val a3 = fleet.length.result

    val count = db.run(
      a1 andThen a2 andThen a3
    ).futureValue

    count shouldBe 0
  }


  // COUNTRY

  it should "insert a country into the database" in {
    val country = newCountry("Spain")
    val query = countries += country
    val action = db.run(query)
    val count = action.futureValue

    count shouldBe 1
  }

  it should "retrieve a country from the database" in {
    insertCountry(spainCountry)

    val list = db.run(countries.filter(_.name === spainCountry.name).result).futureValue

    list.size shouldBe 1
    val country = list.head
    country.id.isDefined shouldBe true
    country.name shouldBe spainCountry.name
  }

  it should "insert a country and an airport" in {
    insertCountry(spainCountry)
    val countryId = db.run(countries.filter(_.name === spainCountry.name).result)
      .futureValue
      .head
      .id.get

    insertAirport(newAirport("Madrid Barajas", barajasIataCode, "LEMD", countryId))
    val airport = db.run(airports.filter(_.iataCode === barajasIataCode).result)
      .futureValue
      .head

    // assert
    airport.id.isDefined shouldBe true
    airport.countryId shouldEqual countryId
  }

  it should "retrieve an Airport empty collection" in{
    val query =  for {
      airport <- airports
      country <- countries.filter(_.name === "spain")
    } yield airport

    val results = db.run(query.result).futureValue

    results.isEmpty shouldBe true
  }

  /*
   _    _   ______   _        _____    ______   _____     _____
  | |  | | |  ____| | |      |  __ \  |  ____| |  __ \   / ____|
  | |__| | | |__    | |      | |__) | | |__    | |__) | | (___
  |  __  | |  __|   | |      |  ___/  |  __|   |  _  /   \___ \
  | |  | | | |____  | |____  | |      | |____  | | \ \   ____) |
  |_|  |_| |______| |______| |_|      |______| |_|  \_\ |_____/
  */

  def newCountry(name: String) = Country(name = name)

  def insertCountry(country: Country) = db.run(countries += country).futureValue

  def newAirport(name: String, iataCode: String, icaoCode: String, countryId: Long) = Airport(None, name, iataCode, icaoCode, countryId)

  def insertAirport(airport: Airport) = db.run(airports += airport).futureValue


  def newAircraft(typeCode: String, registration: String) = Aircraft(typeCode = typeCode, registration = registration)

  def insertAircraft(aircraft: Aircraft): Int = db.run(fleet += aircraft).futureValue

  def createSchema() = db.run(
    (airlines.schema ++
      airports.schema ++
      fleet.schema ++
      countries.schema)
      .create).futureValue

  before {
    db = Database.forConfig("h2mem")
    createSchema()
  }

  after {
    db.close
  }

}
