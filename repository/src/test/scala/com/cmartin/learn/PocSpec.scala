package com.cmartin.learn

import com.cmartin.learn.poc._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable


class PocSpec extends FlatSpec with Matchers with OptionValues with ScalaFutures with BeforeAndAfterEach {
  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val userName = "cmartin"
  val userId = Some(7L)
  val text = "message text"

  implicit var db : Database = _

  /*
  it should "test user entity with default id" in {
    val user = User(userName)

    user.name shouldBe userName
    user.id shouldBe None
  }

  it should "test user entity" in {
    val user = User(userName, userId)

    user.name shouldBe userName
    user.id shouldBe userId
  }

  it should "test message entity" in {
    val message = Message(text, userId.getOrElse(0L))

    message.text shouldBe text
    message.userId shouldBe userId.value
  }
*/


  it should "return zero user count" in new UserRepo {
    val count = userRepository.count()

    val result = count.futureValue

    result shouldBe 0
  }

  it should "return zero message count" in new UserRepo {
    val count = messageRepository.count()

    val result = count.futureValue

    result shouldBe 0
  }

  class UserRepo {
    val userRepository : UserRepository = new UserRepository
    val messageRepository : MessageRepository = new MessageRepository
  }

  override def beforeEach(): Unit = {
    db = Database.forConfig("h2mem")

    val schemaActionList = List(TableQuery[UserTable], TableQuery[MessageTable])

    db.run(DBIO.sequence(schemaActionList.map(_.schema.create)))

  }

  override def afterEach() = {
    db.close()
  }
  //    implicit var db: Database = _


  //  def createSchema() = {
  //    val schemaAction = (
  //      userRepository.entities.schema
  //      ).create
  //
  //    db.run(schemaAction).futureValue
  //  }


  //    db = Database.forConfig("h2mem")
  //    val userRepository = new UserRepository
  //    val schemaAction = (
  //      userRepository.entities.schema
  //      ).create
  //
  //    db.run(schemaAction).futureValue
  //


}

/*
trait DB extends BeforeAndAfterAll { this:  Suite =>
  implicit val db = Database.forConfig("h2mem")
  val userRepository = new UserRepository

  override def beforeAll() = {
    def createSchema() = {
          val schemaAction = (
            userRepository.entities.schema
            ).create

          db.run(schemaAction)
        }
  }
  override def afterAll() = db.close()
}
*/
