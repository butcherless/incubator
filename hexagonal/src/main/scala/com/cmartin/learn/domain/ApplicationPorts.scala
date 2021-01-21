package com.cmartin.learn.domain

import com.cmartin.learn.domain.Model.Airport

import scala.concurrent.Future

object ApplicationPorts {

  trait AirportService {
    def create(airport: Airport): Future[Airport]
    def update(airport: Airport): Future[Airport]
  }

}
