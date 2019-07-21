package com.cmartin.poc.repository2

import java.sql.SQLIntegrityConstraintViolationException

import com.cmartin.learn.test.Constants._
import org.scalatest.OptionValues

import scala.concurrent.{Await, Future}

class RouteRepositorySpec extends BaseRepositorySpec with OptionValues {
  val dal = new DatabaseAccessLayer2(config) {

    import profile.api._

    val countryRepo = new CountryRepository(config.db)
    val airportRepo = new AirportRepository(config.db)
    val routeRepo = new RouteRepository(config.db)

    def createSchema(): Future[Unit] = {
      config.db.run(
        (countries.schema ++ airports.schema ++ routes.schema)
          .create)
    }

    def dropSchema(): Future[Unit] = {
      config.db.run(
        (countries.schema ++ airports.schema ++ routes.schema)
          .drop)
//         config.db.run(routes.schema.drop)
//         config.db.run(airports.schema.drop)
//         config.db.run(countries.schema.drop)
    }
  }

  val barajasAirport = Airport(madAirport._1, madAirport._2, madAirport._3, 0)
  val rodeosAirport = Airport(tfnAirport._1, tfnAirport._2, tfnAirport._3, 0)

  "Route Repository" should "insert a route into the database" in {
    val result = for {
      (oAirportId, dAirportId) <- insertCountryAirport()
      routeId <- dal.routeRepo.insert(Route(madTotfnDistance, oAirportId, dAirportId))
    } yield routeId

    result map { id => assert(id > 0) }
  }

  it should "insert a route sequence into the database" in {
    val result = for {
      (oAirportId, dAirportId) <- insertCountryAirport()
      routeSeq <- dal.routeRepo.insert(
        Seq(Route(madTotfnDistance, oAirportId, dAirportId),
          Route(madTotfnDistance, dAirportId, oAirportId))
      )
    } yield routeSeq

    result map { seq => assert(seq.size == 2) }
  }

  it should "fail to insert a duplicate route into the database" in {
    recoverToSucceededIf[SQLIntegrityConstraintViolationException] {
      for {
        (oAirportId, dAirportId) <- insertCountryAirport()
        _ <- dal.routeRepo.insert(
          Seq(Route(madTotfnDistance, oAirportId, dAirportId), // same route
            Route(madTotfnDistance, oAirportId, dAirportId))
        )
      } yield ()
    }
  }


  it should "fail to insert a route into the database with a missing origin airport" in {
    recoverToSucceededIf[SQLIntegrityConstraintViolationException] {
      for {
        (_, dAirportId) <- insertCountryAirport()
        _ <- dal.routeRepo.insert(Route(madTotfnDistance, 0, dAirportId))
      } yield ()
    }
  }

  it should "fail to insert a route into the database with a missing destination airport" in {
    recoverToSucceededIf[SQLIntegrityConstraintViolationException] {
      for {
        (oAirportId, _) <- insertCountryAirport()
        _ <- dal.routeRepo.insert(Route(madTotfnDistance, oAirportId, 0))
      } yield ()
    }
  }

  it should "find a route by its origin airport" in {
    val result = for {
      (oAirportId, dAirportId) <- insertCountryAirport()
      _ <- dal.routeRepo.insert(Route(madTotfnDistance, oAirportId, dAirportId))
      route <- dal.routeRepo.findByIataOrigin(barajasAirport.iataCode)
    } yield (route, oAirportId, dAirportId)

    result map { tuple =>
      assert(tuple._1.size == 1)
      val route = tuple._1
      assert(route.head.originId == tuple._2)
    }
  }


  it should "find a route by its destination airport" in {
    val result = for {
      (oAirportId, dAirportId) <- insertCountryAirport()
      _ <- dal.routeRepo.insert(Route(madTotfnDistance, oAirportId, dAirportId))
      route <- dal.routeRepo.findByIataDestination(rodeosAirport.iataCode)
    } yield (route, oAirportId, dAirportId)

    result map { tuple =>
      assert(tuple._1.size == 1)
      val route = tuple._1
      assert(route.head.destinationId == tuple._3)
    }
  }


  def insertCountryAirport() = for {
    countryId <- dal.countryRepo.insert(spainCountry)
    oAirportId <- dal.airportRepo.insert(barajasAirport.copy(countryId = countryId))
    dAirportId <- dal.airportRepo.insert(rodeosAirport.copy(countryId = countryId))
  } yield (oAirportId, dAirportId)


  override def beforeEach(): Unit = {
    Await.result(dal.createSchema(), timeout)
  }

  override def afterEach(): Unit = {
    Await.result(dal.dropSchema(), timeout)
  }
}
