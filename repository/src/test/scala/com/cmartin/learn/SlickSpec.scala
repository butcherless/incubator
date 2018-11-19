package com.cmartin.learn

import java.time.{LocalDate, LocalTime}

import com.cmartin.learn.repository.frm._
import com.cmartin.learn.repository.implementation._
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
  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val registrationMIG = "ec-mig"
  val registrationMNS = "ec-mns"
  val barajasIataCode = "MAD"
  val madDestinationCount = 4

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
      airlineId <- AirlineRepository.insertAction(aeaAirline._1, aeaAirline._2)
      aircraftId <- AircraftRepository.insertAction(TypeCodes.BOEING_787_800, registrationMIG, airlineId)
      airlineCount <- AirlineRepository.count.result
      aircraftCount <- AircraftRepository.count().result
      airline <- AirlineRepository.findById(airlineId).result
      aircraft <- AircraftRepository.findById(aircraftId).result
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
      airlineId <- AirlineRepository.insertAction(aeaAirline._1, aeaAirline._2)
      _ <- AircraftRepository.insertAction(TypeCodes.BOEING_787_800, registrationMIG, airlineId)
      aircrafts <- AircraftRepository.findByRegistration(registrationMIG).result
    } yield aircrafts

    val aircrafts = db.run(resultAction).futureValue

    aircrafts.size shouldBe 1
    val aircraft = aircrafts.head
    aircraft.id.value should be > 0L
    aircraft.typeCode shouldBe TypeCodes.BOEING_787_800
    aircraft.registration shouldBe registrationMIG
  }

  ignore should "update an aircraft into the database" in {
    AircraftRepository.insertAction(ecMigAircraft._1, ecMigAircraft._2, 0L) //TODO

    val updateAction = AircraftRepository.findByRegistration(registrationMIG)
      .map(a => a.registration)
      .update(registrationMNS)

    val selectAction = AircraftRepository.findByRegistration(registrationMIG).result

    val list = db.run(updateAction andThen selectAction).futureValue

    list.nonEmpty shouldBe true
    val aircraft = list.head
    aircraft.registration shouldBe registrationMNS
  }

  it should "delete an aircraft from the dataase" in {
    val initialAction = for {
      airlineId <- AirlineRepository.insertAction(aeaAirline._1, aeaAirline._2)
      _ <- AircraftRepository.insertAction(ecMigAircraft._1, ecMigAircraft._2, airlineId)
      count <- AircraftRepository.count.result
    } yield count

    val initialCount = db.run(initialAction).futureValue

    initialCount shouldBe 1

    val finalAction = for {
      _ <- AircraftRepository.findByRegistration(registrationMIG).delete
      count <- AircraftRepository.count.result
    } yield count

    val finalCount = db.run(finalAction).futureValue

    finalCount shouldBe 0
  }


  /*
       COUNTRY
   */

  it should "insert a country into the database" in {
    val query = CountryRepository.insertAction(esCountry._1, esCountry._2)
    val count = db.run(query).futureValue

    count shouldBe 1
  }

  it should "retrieve a country from the database" in {
    db.run(CountryRepository.insertAction(esCountry._1, esCountry._2))
    val countryOption = db.run(CountryRepository.findByCodeQuery(esCountry._2).result.headOption).futureValue

    countryOption.value.id.value should be > 0L
    countryOption.value.name shouldBe esCountry._1
    countryOption.value.code shouldBe esCountry._2
  }


  it should "insert an airport and acountry" in {
    val resultAction = for {
      countryId <- CountryRepository.insertAction(esCountry._1, esCountry._2)
      airportId <- AirportRepository.insertAction(madAirport._1, madAirport._2, madAirport._3, countryId)
      airport <- AirportRepository.findById(airportId).result
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
      airport <- AirportRepository.entities
      _ <- CountryRepository.findByCodeQuery(esCountry._2)
    } yield airport

    val results = db.run(query.result).futureValue

    results.isEmpty shouldBe true
  }

  it should "retrieve destinations airports for an origin airport" in {
    val resultAction = populateDatabase()
    Await.result(db.run(resultAction), 2 seconds)

    val res = db.run(RouteRepository.findDestinationsByOrigin(madAirport._2)).futureValue
    res.size shouldBe madDestinationCount
  }

  it should "retrieve flight by code" in {
    val resultAction = populateDatabase()
    Await.result(db.run(resultAction), 2 seconds)

    val res = db.run(FlightRepository.findByCode(flightUx9059._1)).futureValue

    res.size shouldBe 1
    val flight = res.head
    flight.code shouldBe flightUx9059._1
  }

  it should "retrieve all fligths for a given route" in {
    val resultAction = populateDatabase()
    Await.result(db.run(resultAction), 2 seconds)

    val res = db.run(FlightRepository.findByOrigin("MAD")).futureValue

    res.size shouldBe 1
    val flight = res.head
    flight.code shouldBe flightUx9059._1
  }

  it should "populate the database" in {
    val resultAction = populateDatabase()
    Await.result(db.run(resultAction), 2 seconds)

    val countryCount = db.run(CountryRepository.count.result).futureValue
    val airportCount = db.run(AirportRepository.count.result).futureValue

    countryCount shouldBe 3
    airportCount shouldBe 8

    val esResults = db.run(AirportRepository.findByCountryCode(esCountry._2).result).futureValue
    esResults.nonEmpty shouldBe true
    esResults.size shouldBe 3

    val ukResults = db.run(AirportRepository.findByCountryCode(ukCountry._2).result).futureValue
    ukResults.nonEmpty shouldBe true
    ukResults.size shouldBe 2

    val brResults = db.run(AirportRepository.findByCountryCode(brCountry._2).result).futureValue
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


  def createSchema() = {
    val schemaAction = (
      AirlineRepository.entities.schema ++
        AirportRepository.entities.schema ++
        CountryRepository.entities.schema ++
        AircraftRepository.entities.schema ++
        FlightRepository.entities.schema ++
        JourneyRepository.entities.schema ++
        RouteRepository.entities.schema
      ).create

    db.run(schemaAction).futureValue
  }


  def populateDatabase() = {
    for {
      esId <- CountryRepository.insertAction(esCountry._1, esCountry._2)
      airlineId <- AirlineRepository.insertAction(aeaAirline._1, aeaAirline._2)
      madId <- AirportRepository.insertAction(madAirport._1, madAirport._2, madAirport._3, esId)
      tfnId <- AirportRepository.insertAction(tfnAirport._1, tfnAirport._2, tfnAirport._3, esId)
      bcnId <- AirportRepository.insertAction(bcnAirport._1, bcnAirport._2, bcnAirport._3, esId)
      ukId <- CountryRepository.insertAction(ukCountry._1, ukCountry._2)
      lhrId <- AirportRepository.insertAction(lhrAirport._1, lhrAirport._2, lhrAirport._3, ukId)
      lgwId <- AirportRepository.insertAction(lgwAirport._1, lgwAirport._2, lgwAirport._3, ukId)
      brId <- CountryRepository.insertAction(brCountry._1, brCountry._2)
      _ <- AirportRepository.insertAction(bsbAirport._1, bsbAirport._2, bsbAirport._3, brId)
      _ <- AirportRepository.insertAction(gigAirport._1, gigAirport._2, gigAirport._3, brId)
      _ <- AirportRepository.insertAction(ssaAirport._1, ssaAirport._2, ssaAirport._3, brId)
      madTfnId <- RouteRepository.insertAction(957.0, madId, tfnId)
      - <- RouteRepository.insertAction(671.0, madId, lhrId)
      - <- RouteRepository.insertAction(261.0, madId, bcnId)
      - <- RouteRepository.insertAction(655.0, madId, lgwId)
      - <- RouteRepository.insertAction(261.0, bcnId, madId) // 4 destinations
      - <- RouteRepository.insertAction(261.0, bcnId, lgwId)
      bcnTfnId <- RouteRepository.insertAction(1185.0, bcnId, tfnId) // 3 destinations
      aircraftId <- AircraftRepository.insertAction(ecMigAircraft._1, ecMigAircraft._2, airlineId)
      ux9059Id <- FlightRepository.insertAction(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4, madTfnId)
      d85756Id <- FlightRepository.insertAction(flightD85756._1, flightD85756._2, flightD85756._3, flightD85756._4, bcnTfnId)
      _ <- JourneyRepository.insertAction(journeyTime._1, journeyTime._2, ux9059Id, aircraftId)
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

  val ecMigAircraft = (TypeCodes.BOEING_787_800, registrationMIG)


  val madAirport = ("Madrid Barajas", barajasIataCode, "LEMD")
  val tfnAirport = ("Tenerife Norte", "TFN", "GXCO")
  val bcnAirport = ("Barcelona International", "BCN", "LEBL")
  val lhrAirport = ("London Heathrow", "LHR", "EGLL")
  val lgwAirport = ("London Gatwick", "LGW", "EGKK")
  val bsbAirport = ("Presidente Juscelino Kubistschek International", "BSB", "SBBR")
  val ssaAirport = ("Deputado Luiz Eduardo Magalhães International", "SSA", "SBSV")
  val gigAirport = ("Tom Jobim International Airport", "GIG", "SBGL")

  val aeaAirline = ("Air Europa", LocalDate.of(1986, 11, 21))
  val ibkAirline = ("Norwegian Air International", LocalDate.of(1993, 1, 22))

  val flightUx9059 = ("ux9059", "aea9059", LocalTime.of(7, 5), LocalTime.of(8, 55))
  val flightD85756 = ("d85756", "ibk6ty", LocalTime.of(8, 0), LocalTime.of(10, 25))
  val flightI23942 = ("i23942", "ibs3942", LocalTime.of(8, 40), LocalTime.of(10, 30))

  val journeyTime = (LocalTime.of(7, 19), LocalTime.of(8, 41))
}
