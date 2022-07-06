package com.cmartin.learn.adapter.zio2

import slick.jdbc.JdbcProfile
import slick.lifted.Index
import slick.lifted.ProvenShape

object CountryTableDef
    extends JdbcProfile {

  import api._
  import DatabaseDefinitions.AbstractTable._
  import DatabaseDefinitions._
  import PersistenceModel._

  /* C O U N T R Y
   */
  final class CountryTable(tag: Tag)
      extends LongBasedTable[CountryDbo](tag, TableNames.countries) {

    // property columns:
    def name: Rep[String] = column[String]("NAME")

    def code: Rep[String] = column[String]("CODE")

    def * : ProvenShape[CountryDbo] =
      (name, code, id).<>(CountryDbo.tupled, CountryDbo.unapply)

    // indexes
    def codeIndex: Index =
      index("code_idx", code, unique = true)

    def nameIndex: Index =
      index("name_idx", name, unique = true)
  }
}
