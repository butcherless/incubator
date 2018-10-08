package com.cmartin.domain

import java.time.{LocalDate, LocalDateTime}

import org.neo4j.ogm.annotation.{GeneratedValue, Id, NodeEntity}

import scala.beans.BeanProperty

@NodeEntity
case class AircraftBean(@BeanProperty var regNo: String,
                        @BeanProperty var engineNo: Int,
                        @BeanProperty var name: String,
                        @BeanProperty var deliverDate: LocalDate,
                        @BeanProperty var airline: Airline) {
  @BeanProperty
  @Id
  @GeneratedValue var id: java.lang.Long = null
}

@NodeEntity
case class Airline(@BeanProperty var name: String,
                   @BeanProperty var country: String) {
  @BeanProperty
  @Id
  @GeneratedValue var id: java.lang.Long = null
}

@NodeEntity
case class Route(@BeanProperty var origin: String,
                 @BeanProperty var destination: String) {
  @BeanProperty
  @Id
  @GeneratedValue var id: java.lang.Long = null
}

@NodeEntity
case class Flight(@BeanProperty var number: String,
                  @BeanProperty var departureDate: LocalDateTime,
                  @BeanProperty var arrivalDate: LocalDateTime,
                  @BeanProperty var aircraft: AircraftBean) {
  @BeanProperty
  @Id
  @GeneratedValue var id: java.lang.Long = null
}
