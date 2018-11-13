package com.cmartin.learn

import java.time.{LocalDate, LocalTime}

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
  val madDestinationCount = 4

  val ecMigAircraft = Aircraft(TypeCodes.BOEING_787_800, registrationMIG, 0L)

  val fleet = TableQuery[Fleet]
  val countries = TableQuery[Countries]

  val tableCount = 7
  var db: Database = _


  it should "create the aviation database" in {
    val tables = db.run(MTable.getTables).futureValue

    tables.size shouldBe tableCount
    tables.count(_.name.name == TableNames.airlines) shouldBe 1
    tables.count(_.name.name == TableNames.airports) shouldBe 1
    tables.count(_.name.name == TableNames.countries) shouldBe 1
    tables.count(_.name.name == TableNames.fleet) shouldBe 1
    tables.count(_.name.name == TableNames.flights) shouldBe 1
    tables.count(_.name.name == TableNames.journeys) shouldBe 1
    tables.count(_.name.name == TableNames.routes) shouldBe 1
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
      _ <- fleetReturningId += Aircraft(TypeCodes.BOEING_787_800, registrationMIG, airlineId)
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
    country.code shouldBe esCountry._2
  }

  it should "insert a country and an airport" in {

    val resultAction = for {
      countryId <- insertCountryDBIO(esCountry)
      airportId <- insertAirportDBIO(madAirport)(countryId)
      airport <- findAirportById(airportId)
    } yield (airport, airportId, countryId)

    val result = db.run(resultAction).futureValue

    val airportSeq = result._1
    val airportId = result._2
    val countryId = result._3

    // asserts
    airportSeq.size shouldBe 1
    val airport: Airport = airportSeq.head
    airport.id.value shouldBe airportId
    airport.name shouldBe madAirport._1
    airport.iataCode shouldBe madAirport._2
    airport.icaoCode shouldBe madAirport._3
    airport.countryId shouldBe countryId
  }

  it should "retrieve an Airport empty collection" in {
    val query = for {
      airport <- airports
      _ <- countries.filter(_.name === esCountry._1)
    } yield airport

    val results = db.run(query.result).futureValue

    results.isEmpty shouldBe true
  }

  it should "retrieve destinations airports for an origin airport" in {
    val resultAction = populateDatabase()
    Await.result(db.run(resultAction), 2 seconds)

    val res = db.run(findRouteDestinationsByOrigin(madAirport._2)).futureValue
    res.size shouldBe madDestinationCount
  }

  it should "retrieve flight by code" in {
    val resultAction = populateDatabase()
    Await.result(db.run(resultAction), 2 seconds)

    val res = db.run(flights.filter(_.code === flightUx9059._1).result).futureValue

    res.size shouldBe 1
    val flight = res.head
    flight.code shouldBe flightUx9059._1
  }

  //TODO findFlightsByRoute

  it should "populate the database" in {
    val resultAction = populateDatabase()
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

  def insertRouteDBIO(distance: Double)(originId: Long)(destinationId: Long) =
    routes += Route(distance, originId, destinationId)

  def insertCountryDBIO(countryTuple: (String, String)) =
    countriesReturningId += Country(countryTuple._1, countryTuple._2)

  def insertCountry(country: Country) = db.run(countries += country).futureValue

  def insertAircraft(aircraft: Aircraft): Int = db.run(fleet += aircraft).futureValue

  def insertAirport(airport: Airport) = db.run(airports += airport).futureValue

  def insertAirportDBIO(tuple: (String, String, String))(countryId: Long) =
    airportsReturningId += Airport(tuple._1, tuple._2, tuple._3, countryId)

  def insertFlightDBIO(code: String, alias: String, departure: LocalTime, arrival: LocalTime)(routeId: Long) =
    flightsReturningId += Flight(code, alias, departure, arrival, routeId)

  def airlinesReturningId() = airlines returning airlines.map(_.id)

  def airportsReturningId() = airports returning airports.map(_.id)

  def countriesReturningId() = countries returning countries.map(_.id)

  def fleetReturningId() = fleet returning fleet.map(_.id)

  def flightsReturningId = flights returning flights.map(_.id)


  /*
       F I N D E R S
   */

  def findAirportById(id: Long): DBIO[Seq[Airport]] = airports.filter(_.id === id).result

  def findAirportByCountryCode(code: String) = {
    val query = for {
      airport <- airports
      country <- airport.country if country.code === code
    } yield (airport)

    query.result
  }

  def findRouteDestinationsByOrigin(iataCode: String) = {
    val query = for {
      route <- routes
      airport <- route.destination
      origin <- route.origin if origin.iataCode === iataCode
    } yield airport

    query.result
  }


  def createSchema() = {
    val schemaAction = (
      airlines.schema ++
        airports.schema ++
        countries.schema ++
        fleet.schema ++
        flights.schema ++
        journeys.schema ++
        routes.schema
      ).create

    db.run(schemaAction).futureValue
  }


  def populateDatabase() = {
    for {
      esId <- insertCountryDBIO(esCountry)
      madId <- insertAirportDBIO(madAirport)(esId)
      tfnId <- insertAirportDBIO(tfnAirport)(esId)
      bcnId <- insertAirportDBIO(bcnAirport)(esId)
      ukId <- insertCountryDBIO(ukCountry)
      lhrId <- insertAirportDBIO(lhrAirport)(ukId)
      lgwId <- insertAirportDBIO(lgwAirport)(ukId)
      brId <- insertCountryDBIO(brCountry)
      _ <- insertAirportDBIO(bsbAirport)(brId)
      _ <- insertAirportDBIO(gigAirport)(brId)
      _ <- insertAirportDBIO(ssaAirport)(brId)
      madTfnId <- insertRouteDBIO(957.0)(madId)(tfnId)
      - <- insertRouteDBIO(671.0)(madId)(lhrId)
      - <- insertRouteDBIO(261.0)(madId)(bcnId)
      - <- insertRouteDBIO(655.0)(madId)(lgwId) // 4 destinations
      - <- insertRouteDBIO(261.0)(bcnId)(madId)
      - <- insertRouteDBIO(261.0)(bcnId)(lgwId) // 2 destinations
      _ <- insertFlightDBIO(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4)(madTfnId)
      // _ <- journeyDBIO(depDate, arrDate)(flightId)(aircraftId)
    } yield Unit
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

  val madAirport = ("Madrid Barajas", barajasIataCode, "LEMD")
  val tfnAirport = ("Tenerife Norte", "TFN", "GXCO")
  val bcnAirport = ("Barcelona International", "BCN", "LEBL")
  val lhrAirport = ("London Heathrow", "LHR", "EGLL")
  val lgwAirport = ("London Gatwick", "LGW", "EGKK")
  val bsbAirport = ("Presidente Juscelino Kubistschek International", "BSB", "SBBR")
  val ssaAirport = ("Deputado Luiz Eduardo Magalhães International", "SSA", "SBSV")
  val gigAirport = ("Tom Jobim International Airport", "GIG", "SBGL")

  val aeaAirline = ("Air Europa", LocalDate.of(1986, 11, 21))

  val flightUx9059 = ("ux9059", "aea9059", LocalTime.of(7, 5), LocalTime.of(8, 55))
}
