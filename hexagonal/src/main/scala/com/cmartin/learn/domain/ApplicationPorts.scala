package com.cmartin.learn.domain

import scala.concurrent.Future

import com.cmartin.learn.domain.Model.Airport

object ApplicationPorts {

  trait AirportService {
    def create(airport: Airport): Future[Airport]
    def update(airport: Airport): Future[Airport]
    def delete(airport: Airport): Future[Int]
  }

}
