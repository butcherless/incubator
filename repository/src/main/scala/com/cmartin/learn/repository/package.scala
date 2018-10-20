package com.cmartin.learn.repository

import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape


package object frm {

  case class Aircraft(id: Long, typeCode: String, registration: String)


  class Fleet(tag: Tag) extends Table[(Long, String, String)](tag, "AIRCRAFT") {
    // This is the primary key column:
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def typeCode = column[String]("TYPE_CODE")

    def registration = column[String]("REGISTRATION")

    // Every table needs a * projection with the same type as the table's type parameter
    def * : ProvenShape[(Long, String, String)] = (id, typeCode, registration)
  }

  /*
    class Fleet(tag: Tag) extends Table[Aircraft](tag, "AIRCRAFT") {
      // This is the primary key column:
      def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

      def typeCode = column[String]("TYPE_CODE")

      def registration = column[String]("REGISTRATION")

      // Every table needs a * projection with the same type as the table's type parameter
      def * : ProvenShape[Aircraft] = (id, typeCode, registration) <> (Aircraft.tupled, Aircraft.unapply)

      //TODO
      //def * = id ~ typeCode ~ registration <> (Aircraft.apply _, Aircraft.unapply _)
    }
  */
  lazy val fleet = TableQuery[Fleet]

}
