package com.cmartin.learn.repository


import java.sql.Date

import slick.jdbc.H2Profile.api._


package object frm {

  object TableNames {
    val airlines = "AIRLINES"
    val airports = "AIRPORTS"
    val countries = "COUNTRIES"
    val fleet = "FLEET"
  }

  object TypeCodes {
    val AIRBUS_350_900 = "A359"
    val BOEING_787_800 = "B788"
  }

  /*
       A I R C R A F T
   */
  final case class Aircraft(id: Option[Long] = None, typeCode: String, registration: String)

  final class Fleet(tag: Tag) extends Table[Aircraft](tag, TableNames.fleet) {
    // This is the primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def typeCode = column[String]("TYPE_CODE")

    def registration = column[String]("REGISTRATION")

    def * = (id.?, typeCode, registration) <> (Aircraft.tupled, Aircraft.unapply)
  }

  lazy val fleet = TableQuery[Fleet]


  /*
       A I R L I N E
   */
  final case class Airline(id: Option[Long] = None, name: String, foundationDate: Date)

  final class Airlines(tag: Tag) extends Table[Airline](tag, TableNames.airlines) {
    // This is the primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def foundationDate = column[Date]("FOUNDATION_DATE")

    def * = (id.?, name, foundationDate) <> (Airline.tupled, Airline.unapply)
  }

  lazy val airlines = TableQuery[Airlines]


  /*
       C O U N T R Y
   */
  final case class Country(id: Option[Long] = None, name: String, code: String)

  final class Countries(tag: Tag) extends Table[Country](tag, TableNames.countries) {
    // This is the primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")
    def code = column[String]("CODE")

    def * = (id.?, name, code) <> (Country.tupled, Country.unapply)
  }

  lazy val countries: TableQuery[Countries] = TableQuery[Countries]


  /*
       A I R P O R T
   */
  final case class Airport(id: Option[Long] = None, name: String, iataCode: String, icaoCode: String, countryId: Long)

  final class Airports(tag: Tag) extends Table[Airport](tag, TableNames.airports) {
    // This is the primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def iataCode = column[String]("IATA_CODE")

    def icaoCode = column[String]("ICAO_CODE")

    def countryId = column[Long]("COUNTRY_ID")

    def * = (id.?, name, iataCode, icaoCode, countryId) <> (Airport.tupled, Airport.unapply)

    // foreign keys
    def country = foreignKey("COUNTRY", countryId, TableQuery[Countries])(_.id)
  }

  lazy val airports = TableQuery[Airports]

}
