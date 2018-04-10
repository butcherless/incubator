package com.cmartin.domain

import java.time.{LocalDate, LocalDateTime}

import org.neo4j.ogm.annotation.{GeneratedValue, Id, NodeEntity}

import scala.beans.BeanProperty

@NodeEntity
case class AircraftBean(@BeanProperty @Id @GeneratedValue var id: java.lang.Long = null,
                        @BeanProperty var regNo: String,
                        @BeanProperty var engineNo: Int,
                        @BeanProperty var name: String,
                        @BeanProperty var deliverDate: LocalDate)

@NodeEntity
case class Airline(@BeanProperty @Id @GeneratedValue var id: java.lang.Long = null,
                   @BeanProperty var name: String,
                   @BeanProperty var country: String)

@NodeEntity
case class Route(@BeanProperty @Id @GeneratedValue var id: java.lang.Long = null,
                 @BeanProperty var origin: String,
                 @BeanProperty var destination: String)

@NodeEntity
case class Flight(@BeanProperty @Id @GeneratedValue var id: java.lang.Long = null,
                  @BeanProperty var number: String,
                  @BeanProperty var departureDate: LocalDateTime,
                  @BeanProperty var arrivalDate: LocalDateTime)
