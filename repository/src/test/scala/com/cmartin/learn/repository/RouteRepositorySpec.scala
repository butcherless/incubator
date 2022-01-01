package com.cmartin.learn.repository

import com.cmartin.learn.test.AviationData.Constants._
import org.scalatest.{BeforeAndAfterAll, OptionValues}

import scala.concurrent.Await

abstract class RouteRepositorySpec(path: String)
    extends BaseRepositorySpec(path)
    with OptionValues
    with BeforeAndAfterAll {

  val dal = new DatabaseLayer(config) {
    import profile.api._

    val countryRepo = new CountryRepository
    val airportRepo = new AirportRepository
    val routeRepo   = new RouteRepository

    def createSchema() = {
      config.db.run((countries.schema ++ airports.schema ++ routes.schema).create)
    }

    def dropSchema() = {
      config.db.run((countries.schema ++ airports.schema ++ routes.schema).drop)
    }
  }

  import dal.executeFromDb

  val barajasAirport = Airport(madAirport._1, madAirport._2, madAirport._3, 0)
  val rodeosAirport  = Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, 0)

  behavior of "Route Repository"

  it should "insert a route into the database" in {
    val result = for {
      (oAirportId, dAirportId) <- insertCountryAirport()
      routeId                  <- dal.routeRepo.insert(Route(madTotfnDistance, oAirportId, dAirportId))
    } yield routeId

    result map { id =>
      assert(id > 0)
    }
  }

  it should "insert a route sequence into the database" in {
    val result = for {
      (oAirportId, dAirportId) <- insertCountryAirport()
      routeSeq                 <- dal.routeRepo.insert(
                                    Seq(
                                      Route(madTotfnDistance, oAirportId, dAirportId),
                                      Route(madTotfnDistance, dAirportId, oAirportId)
                                    )
                                  )
    } yield routeSeq

    result map { seq =>
      assert(seq.size == 2)
    }
  }

  it should "fail to insert a duplicate route into the database" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        (oAirportId, dAirportId) <- insertCountryAirport()
        _                        <- dal.routeRepo.insert(
                                      Seq(
                                        Route(madTotfnDistance, oAirportId, dAirportId), // same route
                                        Route(madTotfnDistance, oAirportId, dAirportId)
                                      )
                                    )
      } yield ()
    }
  }

  it should "fail to insert a route into the database with a missing origin airport" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        (_, dAirportId) <- insertCountryAirport()
        _               <- dal.routeRepo.insert(Route(madTotfnDistance, 0, dAirportId))
      } yield ()
    }
  }

  it should "fail to insert a route into the database with a missing destination airport" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        (oAirportId, _) <- insertCountryAirport()
        _               <- dal.routeRepo.insert(Route(madTotfnDistance, oAirportId, 0))
      } yield ()
    }
  }

  it should "find a route by its origin airport" in {
    val result = for {
      (oAirportId, dAirportId) <- insertCountryAirport()
      _                        <- dal.routeRepo.insert(Route(madTotfnDistance, oAirportId, dAirportId))
      routes                   <- dal.routeRepo.findByIataOrigin(barajasAirport.iataCode)
    } yield (routes, oAirportId)

    result map { case (routes, oAirportId) =>
      assert(routes.size == 1)
      assert(routes.head.originId == oAirportId)
    }
  }

  it should "find a route by its destination airport" in {
    val result = for {
      (oAirportId, dAirportId) <- insertCountryAirport()
      _                        <- dal.routeRepo.insert(Route(madTotfnDistance, oAirportId, dAirportId))
      routes                   <- dal.routeRepo.findByIataDestination(rodeosAirport.iataCode)
    } yield (routes, dAirportId)

    result map { case (routes, dAirportId) =>
      assert(routes.size == 1)
      assert(routes.head.destinationId == dAirportId)
    }
  }

  def insertCountryAirport() =
    for {
      countryId  <- dal.countryRepo.insert(spainCountry)
      oAirportId <- dal.airportRepo.insert(barajasAirport.copy(countryId = countryId))
      dAirportId <- dal.airportRepo.insert(rodeosAirport.copy(countryId = countryId))
    } yield (oAirportId, dAirportId)

  //  override def afterEach () :Unit = {
  //    Await.result(dal.routeRepo.deleteAll(), timeout)
  //    Await.result(dal.airportRepo.deleteAll(), timeout)
  //    Await.result(dal.countryRepo.deleteAll(), timeout)
  //  }

  override def beforeEach(): Unit = {
    Await.result(dal.createSchema(), waitTimeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.dropSchema(), waitTimeout)
  }
}
