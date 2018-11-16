package com.cmartin.learn.repository

import java.time.LocalDate

import com.cmartin.learn.repository.frm._
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery


package object implementation {

  object CountryRepository {
    lazy val countries: TableQuery[Countries] = TableQuery[Countries]

    def findByCodeQuery(code: String) = {
      countries.filter(_.code === code) //.result.headOption
    }

    def insertAction(name: String, code: String) =
      entityReturningId += Country(name, code)

    def count() = countries.length //.result

    def entityReturningId() = countries returning countries.map(_.id)
  }


  object AircraftRepository {
    lazy val aircrafts: TableQuery[Fleet] = TableQuery[Fleet]

    def findById(id: Long) = aircrafts.filter(_.id === id)

    def findByRegistration(registration: String) = aircrafts.filter(_.registration === registration)

    def insertAction(typeCode: String, registration: String, airlineId: Long) =
      entityReturningId += Aircraft(typeCode, registration, airlineId)

    def count() = aircrafts.length

    def entityReturningId() = aircrafts returning aircrafts.map(_.id)
  }

  object AirlineRepository {
    lazy val airlines: TableQuery[Airlines] = TableQuery[Airlines]

    def findById(id: Long) = airlines.filter(_.id === id)

    def insertAction(name: String, foundationDate: LocalDate) =
      entityReturningId += Airline(name, foundationDate)

    def count() = airlines.length

    def entityReturningId() = airlines returning airlines.map(_.id)
  }

}