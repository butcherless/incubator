package com.cmartin.learn

import com.cmartin.learn.repository.definition.BaseTable
import com.cmartin.learn.test.Constants
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

import scala.concurrent.Await

abstract class RepositorySpec
    extends FlatSpec
    with Matchers
    with BeforeAndAfterEach
    with ScalaFutures {

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  implicit var db: Database = _

  /**
    * Table list required for every test
    */
  val tableList: Seq[TableQuery[_ <: BaseTable[_]]]

  /**
    * Creates the schema required for each test
    *
    * @return
    */
  def createSchema() = {
    db.run(DBIO.sequence(tableList.map(_.schema.create)))
  }

  override def beforeEach() = {
    db = Database.forConfig("h2mem")
    Await.result(createSchema(), Constants.waitTimeout)
  }

  override def afterEach() = {
    db.close
  }

}
