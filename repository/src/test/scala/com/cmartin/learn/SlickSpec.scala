package com.cmartin.learn

import com.cmartin.learn.repository.frm._
import org.scalatest.OptionValues._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global

class SlickSpec extends FlatSpec with Matchers with BeforeAndAfter with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val registrationMIG = "ec-mig"
  val registrationMNS = "ec-mns"
  val barajasIataCode = "MAD"

  val ecMigAircraft = Aircraft(TypeCodes.BOEING_787_800, registrationMIG)

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
    aircraft.id.value should be > 0L
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
    val query = countries += Country(esCountry._1, esCountry._2)
    val action = db.run(query)
    val count = action.futureValue

    count shouldBe 1
  }

  it should "retrieve a country from the database" in {
    insertCountry(Country(esCountry._1, esCountry._2))

    val list = db.run(countries.filter(_.name === esCountry._1).result).futureValue

    list.size shouldBe 1
    val country = list.head
    country.id.value should be > 0L
    country.name shouldBe esCountry._1
  }

  it should "insert a country and an airport" in {
    def countryIdDBIO(): DBIO[Long] = countriesReturningId() += Country(esCountry._1, esCountry._2)

    def airportIdDBIO(id: Long): DBIO[Long] = airportsReturningId += Airport(madAirport._1, madAirport._2, madAirport._3, id)

    def airportDBIO(id: Long): DBIO[Seq[Airport]] = airports.filter(_.id === id).result

    val resultAction = for {
      countryId <- countryIdDBIO
      airportId <- airportIdDBIO(countryId)
      airport <- airportDBIO(airportId)
    } yield (airport, airportId, countryId)

    val result = db.run(resultAction).futureValue

    val airportSeq = result._1
    val airportId = result._2
    val countryId = result._3

    // assert
    airportSeq.nonEmpty shouldBe true
    val airport: Airport = airportSeq.head
    airport.id.value shouldBe airportId
    airport.iataCode shouldBe madAirport._2
    airport.countryId shouldBe countryId
  }

  it should "retrieve an Airport empty collection" in {
    val query = for {
      airport <- airports
      country <- countries.filter(_.name === esCountry._1)
    } yield airport

    val results = db.run(query.result).futureValue

    results.isEmpty shouldBe true
  }


  it should "populate the database" in {
    db.run(countries ++= TestCountries)
    val countryId = db.run(countries.filter(_.code === esCountry._2).result)
      .futureValue
      .head
      .id

    insertAirport(Airport(madAirport._1, madAirport._2, madAirport._3, countryId.get))
    insertAirport(Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, countryId.get))
    insertAirport(Airport(bcnAirport._1, bcnAirport._2, bcnAirport._3, countryId.get))

    val countryCount = db.run(countries.length.result).futureValue
    val airportCount = db.run(airports.length.result).futureValue

    countryCount shouldBe 3
    airportCount shouldBe 3

    val results = db.run(findAirportByCountryName(esCountry._1)).futureValue

    results.nonEmpty shouldBe true
    results.size shouldBe 3
  }

  /*
   _    _   ______   _        _____    ______   _____     _____
  | |  | | |  ____| | |      |  __ \  |  ____| |  __ \   / ____|
  | |__| | | |__    | |      | |__) | | |__    | |__) | | (___
  |  __  | |  __|   | |      |  ___/  |  __|   |  _  /   \___ \
  | |  | | | |____  | |____  | |      | |____  | | \ \   ____) |
  |_|  |_| |______| |______| |_|      |______| |_|  \_\ |_____/
  */

  def newCountry(name: String, code: String) = Country(name, code)

  def insertCountry(country: Country) = db.run(countries += country).futureValue

  def airportsReturningId() = airports returning airports.map(_.id)

  def countriesReturningId() = countries returning countries.map(_.id)

  def insertAirport(airport: Airport) = db.run(airports += airport).futureValue

  def newAircraft(typeCode: String, registration: String) = Aircraft(typeCode = typeCode, registration = registration)

  def insertAircraft(aircraft: Aircraft): Int = db.run(fleet += aircraft).futureValue

  // F I N D E R S
  def findAirportByCountryName(name: String) = {
    val query = for {
      airport <- airports
      country <- countries.filter(_.name === name)
    } yield airport

    query.result
  }


  def createSchema() = {
    val schemaAction = (
      airlines.schema ++
        airports.schema ++
        fleet.schema ++
        countries.schema
      ).create

    db.run(schemaAction).futureValue
  }


  before {
    db = Database.forConfig("h2mem")
    createSchema()
  }

  after {
    db.close
  }


  // T E S T   D A T A
  val esCountry = ("Spain", "ES")
  val ukCountry = newCountry("United Kingdom", "UK")
  val brCountry = newCountry("Brasil", "BR")

  val TestCountries = Seq(Country(esCountry._1, esCountry._2), ukCountry, brCountry)

  val madAirport = ("Madrid Barajas", "MAD", "LEMD")
  val tfnAirport = ("Tenerife Norte", "TFN", "GXCO")
  val bcnAirport = ("Barcelona International", "BCN", "LEBL")
  /*
  val lhrAirport = Airport(None, "London Heathrow", "LHR", "EGLL")
  val lgwAirport = Airport(None, "London Gatwick", "LGW", "EGKK")
  val bsbAirport = Airport(None, "Presidente Juscelino Kubistschek International", "BSB", "SBBR")
  val ssaAirport = Airport(None, "Deputado Luiz Eduardo Magalhães International", "SSA", "SBSV")
  val gigAirport = Airport(None, "Tom Jobim International Airport", "GIG", "SBGL")
  */

}
