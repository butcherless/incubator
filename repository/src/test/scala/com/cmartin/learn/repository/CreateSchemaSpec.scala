package com.cmartin.learn.repository

/**
  * Spec helper for development
  */
class CreateSchemaSpec extends BaseRepositorySpec {

  val dal = new DatabaseAccessLayer2(config) {

    import profile.api._

    val countryRepo = new CountryRepository
    val airlineRepo = new AirlineRepository

    def printSchema(): String = {
      (
        countries.schema ++
          airlines.schema ++
          airports.schema ++
          fleet.schema ++
          routes.schema
        )
        .createStatements.mkString("\n")
    }

  }

  "Schema" should "print the database schema for copy & paste" in {
    val keywords = Seq("create", "index", "table", "constraint", "foreign", "alter")
    val result = dal.printSchema()
    info(result) // for copy & paste
    assert(keywords.forall(kw => result.contains(kw)))
  }

}