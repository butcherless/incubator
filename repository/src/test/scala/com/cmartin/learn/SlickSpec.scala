package com.cmartin.learn

import java.time.{LocalDate, LocalTime}

import com.cmartin.learn.repository.implementation._
import com.cmartin.learn.repository.tables._
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
