package com.cmartin.learn

import java.time.{LocalDate, LocalTime}

import com.cmartin.learn.repository.frm._
import com.cmartin.learn.repository.implementation._
import com.cmartin.learn.test.Constants
import org.scalatest.OptionValues._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

class SlickSpec extends FlatSpec with Matchers with BeforeAndAfterEach with ScalaFutures {
  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  implicit var db: Database = _


  it should "insert an aircraft into the database" in new Repos {

    val aircraftFuture = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airlineId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      aircraftId <- aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, Constants.registrationMIG, airlineId))
    } yield aircraftId

    val aircraftId = aircraftFuture.futureValue.value
    aircraftId should be > 0L
  }

  it should "retrieve an aircraft from the database" in new Repos {

    val aircraftFuture: Future[Option[Aircraft]] = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airlineId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      aircraftId <- aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, Constants.registrationMIG, airlineId))
      aircraft <- aircraftRepo.findById(aircraftId)
    } yield aircraft

    val aircraft = aircraftFuture.futureValue.value
    aircraft.id.value should be > 0L
    aircraft.typeCode shouldEqual TypeCodes.BOEING_787_800
    aircraft.registration shouldEqual Constants.registrationMIG
    aircraft.airlineId should be > 0L
  }

  it should "update an aircraft into the database" in new Repos {
    val aircraftResult = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airlineId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      _ <- aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, Constants.registrationMIG, airlineId))
      aircraft <- aircraftRepo.findByRegistration(Constants.registrationMIG)
      _ <- aircraftRepo.update(aircraft.value.copy(registration = Constants.registrationMNS))
      aircraft <- aircraftRepo.findByRegistration(Constants.registrationMNS)
    } yield aircraft

    val aircraft = aircraftResult.futureValue
    aircraft.value.registration shouldBe Constants.registrationMNS
  }

  it should "delete an aircraft from the dataase" in new Repos {
    val initialCount = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airlineId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, countryId))
      _ <- aircraftRepo.insert(Aircraft(TypeCodes.BOEING_787_800, Constants.registrationMIG, airlineId))
      count <- aircraftRepo.count
    } yield count

    initialCount.futureValue shouldBe 1

    val finalCount = for {
      aircraft <- aircraftRepo.findByRegistration(Constants.registrationMIG)
      _ <- aircraftRepo.delete(aircraft.value.id.value)
      count <- aircraftRepo.count
    } yield count

    finalCount.futureValue shouldBe 0
  }

  it should "retrieve an airline from the database" in new Repos {
    val airlineOption = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airlineId <- airlineRepo.insert(Airline(ibsAirline._1, ibsAirline._2, countryId))
      airline <- airlineRepo.findById(airlineId)
    } yield airline

    val airline: Option[Airline] = airlineOption.futureValue

    airline.value.id.value should be > 0L
    airline.value.name shouldBe ibsAirline._1
    airline.value.foundationDate shouldBe ibsAirline._2
  }

  /*
       COUNTRY
   */

  it should "insert a country into the database" in new Repos {
    val count = countryRepo.insert(Country(esCountry._1, esCountry._2))

    count.futureValue shouldBe 1
  }

  it should "insert a sequence of countries into the database" in new Repos {
    val countrySequence = Seq(Country(esCountry._1, esCountry._2), Country(ukCountry._1, ukCountry._2))
    val ids = countryRepo.insert(countrySequence).futureValue

    ids.nonEmpty shouldBe true
    ids.size shouldBe countrySequence.size
    ids.forall(_ > 0L) shouldBe true
  }

  it should "retrieve a country from the database" in new Repos {
    countryRepo.insert(Country(esCountry._1, esCountry._2))

    val countryOption = countryRepo.findByCode(esCountry._2).futureValue

    countryOption.value.id.value should be > 0L
    countryOption.value.name shouldBe esCountry._1
    countryOption.value.code shouldBe esCountry._2
  }

  it should "update a country from the database" in new Repos {
    val countryId = countryRepo.insert(Country(esCountry._1, esCountry._2)).futureValue
    val updateResult = countryRepo.update(Country(esCountry._1.toUpperCase, esCountry._2.toUpperCase, Option(countryId)))
    val countryOption = countryRepo.findById(countryId).futureValue
    val countryCount = countryRepo.count().futureValue

    countryId should be > 0L
    updateResult.futureValue shouldBe 1
    countryCount shouldBe 1
    countryOption.value.code.forall(_.isUpper) shouldBe true
    countryOption.value.name.forall(_.isUpper) shouldBe true
    countryOption.value.id.value shouldBe countryId
  }

  it should "delete a country from the database" in new Repos {
    val countryId = countryRepo.insert(Country(esCountry._1, esCountry._2)).futureValue
    val initialCount = countryRepo.count().futureValue
    val deleteResult = countryRepo.delete(countryId).futureValue
    val finalCount = countryRepo.count().futureValue

    countryId should be > 0L
    initialCount shouldBe 1
    deleteResult shouldBe 1
    finalCount shouldBe 0
  }

  /*
      A I R P O R T
   */

  it should "insert an airport and a country" in new Repos {

    val airportOption = for {
      countryId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
      airportId <- airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, countryId))
      airport <- airportRepo.findById(airportId)
    } yield airport


    // asserts
    val airport = airportOption.futureValue.value

    airport.id.value should be > 0L
    airport.countryId should be > 0L
    airport.name shouldBe madAirport._1
    airport.iataCode shouldBe madAirport._2
    airport.icaoCode shouldBe madAirport._3
  }


  it should "retrieve an Airport empty collection" in new Repos {
    val countries = airportRepo.findByCountryCode(esCountry._2).futureValue

    countries.isEmpty shouldBe true
  }

  it should "retrieve destinations airports for an origin airport" in new Repos {
    Await.result(populateDatabase, Constants.waitTimeout)

    val destinations = routeRepo.findByIataOrigin(madAirport._2).futureValue
    destinations.size shouldBe Constants.madDestinationCount
  }

  it should "retrieve flight by code" in new Repos {
    Await.result(populateDatabase, Constants.waitTimeout)

    val flightOption = flightRepo.findByCode(flightUx9059._1).futureValue

    flightOption.value.code shouldBe flightUx9059._1
    flightOption.value.alias shouldBe flightUx9059._2
  }

  it should "retrieve all flights for a given route" in new Repos {
    Await.result(populateDatabase, Constants.waitTimeout)
    val expectedFlightCount = 2
    val expectedSet = Set(flightUx9059._1, flightI23942._1)

    val flights = flightRepo.findByOrigin(Constants.barajasIataCode).futureValue

    flights.size shouldBe expectedFlightCount
    flights.map(_.code).toSet diff expectedSet shouldBe Set.empty
  }

  it should "retrieve aircraft list from an airline" in new Repos {
    Await.result(populateDatabase, Constants.waitTimeout)
    val expectedAircfraftCount = 2

    val aircrafts = aircraftRepo.findByAirlineName(aeaAirline._1).futureValue

    aircrafts.size shouldBe expectedAircfraftCount
  }

  it should "retrieve airline list from a country" in new Repos {
    Await.result(populateDatabase, Constants.waitTimeout)
    val expectedAirlineCount = 2

    val airlines = airlineRepo.findByCountryCode(esCountry._2).futureValue

    airlines.size shouldBe expectedAirlineCount
  }

  it should "retrieve airport list from a country" in new Repos {
    Await.result(populateDatabase, Constants.waitTimeout)
    val expectedCountryCount = 3

    val airports = airportRepo.findByCountryCode(esCountry._2).futureValue
    airports.size shouldBe expectedCountryCount
  }

  it should "retrieve route list from its origin" in new Repos {
    Await.result(populateDatabase, Constants.waitTimeout)
    val expectedRouteCount = 3

    val routes = routeRepo.findByIataOrigin(bcnAirport._2).futureValue

    routes.size shouldBe expectedRouteCount
  }

  it should "retrieve route list from its destination" in new Repos {
    Await.result(populateDatabase, Constants.waitTimeout)
    val expectedRouteCount = 2

    val routes = routeRepo.findByIataDestination(tfnAirport._2).futureValue

    routes.size shouldBe expectedRouteCount
  }


  /*
   _    _   ______   _        _____    ______   _____     _____
  | |  | | |  ____| | |      |  __ \  |  ____| |  __ \   / ____|
  | |__| | | |__    | |      | |__) | | |__    | |__) | | (___
  |  __  | |  __|   | |      |  ___/  |  __|   |  _  /   \___ \
  | |  | | | |____  | |____  | |      | |____  | | \ \   ____) |
  |_|  |_| |______| |______| |_|      |______| |_|  \_\ |_____/
  */


  def populateDatabase() = {
    new Repos {
      val insert = for {
        brId <- countryRepo.insert(Country(brCountry._1, brCountry._2))
        esId <- countryRepo.insert(Country(esCountry._1, esCountry._2))
        noId <- countryRepo.insert(Country(noCountry._1, noCountry._2))
        ukId <- countryRepo.insert(Country(ukCountry._1, ukCountry._2))

        aeaId <- airlineRepo.insert(Airline(aeaAirline._1, aeaAirline._2, esId))
        ibsId <- airlineRepo.insert(Airline(ibsAirline._1, ibsAirline._2, esId))
        ibkId <- airlineRepo.insert(Airline(ibkAirline._1, ibkAirline._2, noId))

        madId <- airportRepo.insert(Airport(madAirport._1, madAirport._2, madAirport._3, esId))
        tfnId <- airportRepo.insert(Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, esId))
        bcnId <- airportRepo.insert(Airport(bcnAirport._1, bcnAirport._2, bcnAirport._3, esId))
        lhrId <- airportRepo.insert(Airport(lhrAirport._1, lhrAirport._2, lhrAirport._3, ukId))
        lgwId <- airportRepo.insert(Airport(lgwAirport._1, lgwAirport._2, lgwAirport._3, ukId))
        _ <- airportRepo.insert(Airport(bsbAirport._1, bsbAirport._2, bsbAirport._3, brId))
        _ <- airportRepo.insert(Airport(gigAirport._1, gigAirport._2, gigAirport._3, brId))
        _ <- airportRepo.insert(Airport(ssaAirport._1, ssaAirport._2, ssaAirport._3, brId))

        madTfnId <- routeRepo.insert(Route(957.0, madId, tfnId))
        - <- routeRepo.insert(Route(671.0, madId, lhrId))
        - <- routeRepo.insert(Route(261.0, madId, bcnId))
        - <- routeRepo.insert(Route(655.0, madId, lgwId))
        - <- routeRepo.insert(Route(261.0, bcnId, madId)) // 4 destinations
        - <- routeRepo.insert(Route(599.0, bcnId, lgwId))
        bcnTfnId <- routeRepo.insert(Route(1185.0, bcnId, tfnId)) // 3 destinations

        aircraftId <- aircraftRepo.insert(Aircraft(ecMigAircraft._1, ecMigAircraft._2, aeaId))
        _ <- aircraftRepo.insert(Aircraft(ecLvlAircraft._1, ecLvlAircraft._2, aeaId))

        ux9059Id <- flightRepo.insert(Flight(flightUx9059._1, flightUx9059._2, flightUx9059._3, flightUx9059._4, aeaId, madTfnId))
        _ <- flightRepo.insert(Flight(flightI23942._1, flightI23942._2, flightI23942._3, flightI23942._4, ibsId, madTfnId))
        d85756Id <- flightRepo.insert(Flight(flightD85756._1, flightD85756._2, flightD85756._3, flightD85756._4, ibkId, bcnTfnId))

        _ <- journeyRepo.insert(Journey(journeyTime._1, journeyTime._2, ux9059Id, aircraftId))
      } yield ()
    }.insert
  }


  trait Repos {
    val aircraftRepo = new AircraftRepository
    val airlineRepo = new AirlineRepository
    val airportRepo = new AirportRepository
    val countryRepo = new CountryRepository
    val routeRepo = new RouteRepository
    val flightRepo = new FlightRepository
    val journeyRepo = new JourneyRepository
  }


  def createSchema() = {
    val schemaActionList = List(
      TableQuery[Countries],
      TableQuery[Airlines],
      TableQuery[Airports],
      TableQuery[Fleet],
      TableQuery[Routes],
      TableQuery[Flights],
      TableQuery[Journeys]
    )
    db.run(DBIO.sequence(schemaActionList.map(_.schema.create)))
  }


  override def beforeEach() = {
    db = Database.forConfig("h2mem")
    Await.result(createSchema(), Constants.waitTimeout)
  }

  override def afterEach() = db.close

  /*
    _____         _     ____        _
   |_   _|__  ___| |_  |  _ \  __ _| |_ __ _
     | |/ _ \/ __| __| | | | |/ _` | __/ _` |
     | |  __/\__ \ |_  | |_| | (_| | || (_| |
     |_|\___||___/\__| |____/ \__,_|\__\__,_|

   */

  val esCountry = ("Spain", "ES")
  val noCountry = ("Norway", "NO")
  val ukCountry = ("United Kingdom", "UK")
  val brCountry = ("Brasil", "BR")

  val TestCountries = Seq(Country(esCountry._1, esCountry._2), ukCountry, brCountry)

  val ecMigAircraft = (TypeCodes.BOEING_787_800, Constants.registrationMIG)
  val ecLvlAircraft = (TypeCodes.AIRBUS_330_200, Constants.registrationLVL)


  val madAirport = ("Madrid Barajas", Constants.barajasIataCode, "LEMD")
  val tfnAirport = ("Tenerife Norte", "TFN", "GXCO")
  val bcnAirport = ("Barcelona International", "BCN", "LEBL")
  val lhrAirport = ("London Heathrow", "LHR", "EGLL")
  val lgwAirport = ("London Gatwick", "LGW", "EGKK")
  val bsbAirport = ("Presidente Juscelino Kubistschek International", "BSB", "SBBR")
  val ssaAirport = ("Deputado Luiz Eduardo Magalhães International", "SSA", "SBSV")
  val gigAirport = ("Tom Jobim International Airport", "GIG", "SBGL")

  val aeaAirline = ("Air Europa", LocalDate.of(1986, 11, 21))
  val ibsAirline = ("Iberia Express", LocalDate.of(2011, 10, 6))
  val ibkAirline = ("Norwegian Air International", LocalDate.of(1993, 1, 22))

  val flightUx9059 = ("ux9059", "aea9059", LocalTime.of(7, 5), LocalTime.of(8, 55))
  val flightD85756 = ("d85756", "ibk6ty", LocalTime.of(8, 0), LocalTime.of(10, 25))
  val flightI23942 = ("i23942", "ibs3942", LocalTime.of(8, 40), LocalTime.of(10, 30))

  val journeyTime = (LocalTime.of(7, 19), LocalTime.of(8, 41))
}
