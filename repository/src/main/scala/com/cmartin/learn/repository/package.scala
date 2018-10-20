package com.cmartin.learn.repository

import slick.jdbc.H2Profile.api._


package object frm {

  object TableNames {
    val fleet = "FLEET"
  }

  object TypeCodes {
    val AIRBUS_350_900 = "A359"
    val BOEING_787_800 = "B788"
  }

  case class Aircraft(id: Option[Long], typeCode: String, registration: String)

  class Fleet(tag: Tag) extends Table[Aircraft](tag, TableNames.fleet) {
    // This is the primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def typeCode = column[String]("TYPE_CODE")

    def registration = column[String]("REGISTRATION")

    def * = (id.?, typeCode, registration) <> (Aircraft.tupled, Aircraft.unapply)
  }

  lazy val fleet = TableQuery[Fleet]

}
