package com.cmartin.learn

import java.sql.Date
import java.time.LocalDate

import com.cmartin.learn.repository.frm._
import org.scalatest.OptionValues._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class SlickSpec extends FlatSpec with Matchers with BeforeAndAfter with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val registrationMIG = "ec-mig"
  val registrationMNS = "ec-mns"
  val barajasIataCode = "MAD"

  val ecMigAircraft = Aircraft(TypeCodes.BOEING_787_800, registrationMIG, 0L)

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

    val resultAction = for {
      airlineId <- airlinesReturningId() += Airline(aeaAirline._1, aeaAirline._2)
      aircraftId <- fleetReturningId += Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId)
      airlineCount <- airlines.length.result
      aircraftCount <- fleet.length.result
      airline <- airlines.filter(_.id === airlineId).result
      aircraft <- fleet.filter(_.id === aircraftId).result
    } yield (airlineCount, airline, aircraftCount, aircraft)

    val results = db.run(resultAction).futureValue

    results._1 shouldBe 1
    results._2.nonEmpty shouldBe true
    val airline = results._2.head
    airline.name shouldEqual aeaAirline._1
    airline.foundationDate shouldEqual aeaAirline._2

    results._3 shouldBe 1
    results._2.nonEmpty shouldBe true
    val aircraft = results._4.head
    aircraft.typeCode shouldEqual TypeCodes.BOEING_787_800
    aircraft.registration shouldEqual registrationMIG
    aircraft.airlineId shouldEqual airline.id.value
  }

  it should "retrieve an aircraft from the database" in {

    val resultAction = for {
      airlineId <- airlinesReturningId() += Airline(aeaAirline._1, aeaAirline._2)
      aircraftId <- fleetReturningId += Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId)
      aircrafts <- fleet.filter(_.registration === registrationMIG).result
    } yield (aircrafts)

    val aircrafts = db.run(resultAction).futureValue


    aircrafts.size shouldBe 1
    val aircraft = aircrafts.head
    aircraft.id.value should be > 0L
    aircraft.typeCode shouldBe TypeCodes.BOEING_787_800
    aircraft.registration shouldBe registrationMIG
  }

  ignore should "update an aircraft into the database" in {
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

  ignore should "delete an aircraft from the dataase" in {
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

  def countryIdDBIO(countryTuple: (String, String)) = countriesReturningId() += Country(countryTuple._1, countryTuple._2)

  def airportIdDBIO(tuple: (String, String, String))(countryId: Long) =
    airportsReturningId() += Airport(tuple._1, tuple._2, tuple._3, countryId)

  it should "populate the database" in {
    val resultAction = for {
      esId <- countryIdDBIO(esCountry)
      _ <- airportIdDBIO(madAirport)(esId)
      _ <- airportIdDBIO(tfnAirport)(esId)
      _ <- airportIdDBIO(bcnAirport)(esId)
      ukId <- countryIdDBIO(ukCountry)
      _ <- airportIdDBIO(lhrAirport)(ukId)
      _ <- airportIdDBIO(lgwAirport)(ukId)
      brId <- countryIdDBIO(brCountry)
      _ <- airportIdDBIO(bsbAirport)(brId)
      _ <- airportIdDBIO(gigAirport)(brId)
      _ <- airportIdDBIO(ssaAirport)(brId)
    } yield Unit

    Await.result(db.run(resultAction), 2 seconds)


    val countryCount = db.run(countries.length.result).futureValue
    val airportCount = db.run(airports.length.result).futureValue

    countryCount shouldBe 3
    airportCount shouldBe 8

    val esResults = db.run(findAirportByCountryCode(esCountry._2)).futureValue
    esResults.nonEmpty shouldBe true
    esResults.size shouldBe 3

    val ukResults = db.run(findAirportByCountryCode(ukCountry._2)).futureValue
    ukResults.nonEmpty shouldBe true
    ukResults.size shouldBe 2

    val brResults = db.run(findAirportByCountryCode(brCountry._2)).futureValue
    brResults.nonEmpty shouldBe true
    brResults.size shouldBe 3

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

  def airlinesReturningId() = airlines returning airlines.map(_.id)

  def airportsReturningId() = airports returning airports.map(_.id)

  def countriesReturningId() = countries returning countries.map(_.id)

  def fleetReturningId() = fleet returning fleet.map(_.id)

  def insertAirport(airport: Airport) = db.run(airports += airport).futureValue

  def insertAircraft(aircraft: Aircraft): Int = db.run(fleet += aircraft).futureValue

  def newLocalDate(year: Int, month: Int, day: Int): Date = java.sql.Date.valueOf(LocalDate.of(year, month, day))


  // F I N D E R S
  def findAirportByCountryCode(code: String) = {
    val query = for {
      airport <- airports
      //country <- countries if country.id === airport.countryId && country.code === code
      country <- airport.country if country.code === code
    } yield (airport)

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
  val ukCountry = ("United Kingdom", "UK")
  val brCountry = ("Brasil", "BR")

  val TestCountries = Seq(Country(esCountry._1, esCountry._2), ukCountry, brCountry)

  val madAirport = ("Madrid Barajas", "MAD", "LEMD")
  val tfnAirport = ("Tenerife Norte", "TFN", "GXCO")
  val bcnAirport = ("Barcelona International", "BCN", "LEBL")
  val lhrAirport = ("London Heathrow", "LHR", "EGLL")
  val lgwAirport = ("London Gatwick", "LGW", "EGKK")
  val bsbAirport = ("Presidente Juscelino Kubistschek International", "BSB", "SBBR")
  val ssaAirport = ("Deputado Luiz Eduardo Magalhães International", "SSA", "SBSV")
  val gigAirport = ("Tom Jobim International Airport", "GIG", "SBGL")

  val aeaAirline = ("Air Europa", newLocalDate(1986, 11, 21))
}
