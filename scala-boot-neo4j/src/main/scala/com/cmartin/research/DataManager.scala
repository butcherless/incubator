package com.cmartin.research

import java.time.LocalDate
import java.util.UUID

import org.neo4j.driver.v1.{AuthTokens, GraphDatabase}
import org.slf4j.{Logger, LoggerFactory}

object DataManager {
  val log: Logger = LoggerFactory.getLogger(DataManager.getClass)

  //case class Airline(name: String, country: String)

  case class Aircraft(id: String,
                      regNo: String,
                      engineNo: Int,
                      name: String,
                      deliverDate: LocalDate)

  def getDriver = GraphDatabase.driver("bolt://localhost/7687", AuthTokens.basic("neo4j", "kagarro"))

  def getId = UUID.randomUUID().toString

  def getLocalDate(year: Int, month: Int, day: Int) = LocalDate.of(year, month, day)

  def loadData = {
    val driver = getDriver
    val session = driver.session

    val delete_all_stmt = "match (n) detach delete (n)"
    session.run(delete_all_stmt)
/*
    val airlines = List(
      Airline("Air Europa", "ES"),
      Airline("Iberia", "ES"),
      Airline("Delta", "US")
    )
    airlines.foreach(
      e => session.run(s"create (a:Airline {name:'${e.name}', country:'${e.country}'})")
    )
*/
    val aircrafts = List(
      Aircraft(getId, "ec-mmx", 2, "Sierra Nevada", LocalDate.of(2018, 2, 15)),
      Aircraft(getId, "ec-lvl", 2, "Aneto", LocalDate.of(2012, 5, 1))
    )
    //s"MATCH (user:Users) where user.name ='$name' SET user.name = '$newName' RETURN user.name AS name, user.last_name AS last_name, user.age AS age, user.city AS city"
    val airline = session.run("match (a:Airline) where a.name = 'Iberia' return (a)").next

    val ac = aircrafts(0)
    session.run(s"create (a:Aircraft {id:'${ac.id}', name:'${ac.name}', deliverDate:'${ac.deliverDate}'})")
    /*
    aircrafts.foreach(
      e =>
    )
*/
    session.close
    driver.close
  }
}
