package com.cmartin.learn

import com.cmartin.learn.repository.implementation._
import com.cmartin.learn.repository.tables._
import com.cmartin.learn.test.Constants._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class RouteRepositorySpec extends RepositorySpec {
  val tableList = List(
    TableQuery[Countries],
    TableQuery[Airlines],
    TableQuery[Fleet],
    TableQuery[Airports],
    TableQuery[Routes]
  )

  trait Repos {
    val airlineRepo = new AirlineRepository
    val airportRepo = new AirportRepository
    val countryRepo = new CountryRepository
    val routeRepo = new RouteRepository
  }


  it should "retrieve destinations airports for an origin airport" in new Repos {
    Await.result(populateDatabase, waitTimeout)

    val destinations = routeRepo.findByIataOrigin(madAirport._2).futureValue
    destinations.size shouldBe madDestinationCount
  }

  it should "retrieve route list from its origin" in new Repos {
    Await.result(populateDatabase, waitTimeout)
    val expectedRouteCount = 3

    val routes = routeRepo.findByIataOrigin(bcnAirport._2).futureValue

    routes.size shouldBe expectedRouteCount
  }

  it should "retrieve route list from its destination" in new Repos {
    Await.result(populateDatabase, waitTimeout)
    val expectedRouteCount = 2

    val routes = routeRepo.findByIataDestination(tfnAirport._2).futureValue

    routes.size shouldBe expectedRouteCount
  }

  def populateDatabase() = {
    new Repos {
      val result = for {

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


        madTfnId <- routeRepo.insert(Route(957.0, madId, tfnId))
        - <- routeRepo.insert(Route(671.0, madId, lhrId))
        - <- routeRepo.insert(Route(261.0, madId, bcnId))
        - <- routeRepo.insert(Route(655.0, madId, lgwId))
        - <- routeRepo.insert(Route(261.0, bcnId, madId)) // 4 destinations
        - <- routeRepo.insert(Route(599.0, bcnId, lgwId))
        bcnTfnId <- routeRepo.insert(Route(1185.0, bcnId, tfnId)) // 3 destinations

      } yield ()
    }.result
  }
}
