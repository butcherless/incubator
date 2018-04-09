package com.cmartin.research

import java.time.LocalDate

import com.cmartin.repository.PersonRepository
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.{ApplicationRunner, SpringApplication}
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

import scala.beans.BeanProperty

@Configuration
@ComponentScan
@EnableAutoConfiguration
//@SpringBootApplication
class ScalaApplication {
  val log: Logger = LoggerFactory.getLogger(classOf[ScalaApplication])

  // bean declaration

  case class AircraftBean(@BeanProperty id: Long,
                          @BeanProperty regNo: String,
                          @BeanProperty engineNo: Int,
                          @BeanProperty airlineName: String,
                          @BeanProperty deliverDate: LocalDate)

  case class Aircraft(id: String,
                      regNo: String,
                      engineNo: Int,
                      airlineName: String,
                      deliverDate: LocalDate)

  class DependencyAnalyzer {
    def hello = s"scala class ${this.toString}"
  }

  //@NodeEntity
  //class Person(@BeanProperty @Id @GeneratedValue id: java.lang.Long, @BeanProperty name: String)


  @Configuration
  @EnableNeo4jRepositories(Array("com.cmartin.repository"))
  @EnableTransactionManagement
  class MyConfiguration {
    @Bean def dependencyAnalyzer() = new DependencyAnalyzer()

    @Bean def configuration() = {
      new org.neo4j.ogm.config.Configuration.Builder()
        .uri("bolt://localhost")
        .credentials("neo4j", "kagarro")
        .build()
    }

    @Bean
    def sessionFactory(): SessionFactory = {
      // with domain entity base package(s)
      new SessionFactory(configuration(), "com.cmartin.domain")
    }

    @Bean
    def transactionManager(): Neo4jTransactionManager = {
      new Neo4jTransactionManager(sessionFactory())
    }
  }


  @Bean
  //def init(da: DependencyAnalyzer): ApplicationRunner = args => {
  def init(repo: PersonRepository): ApplicationRunner = args => {
    log.debug("ScalaApplication/SpringBoot initialization")
    val count = repo.count()
    log.debug(s"person entity count: ${count}")
    //DataManager.loadData
    log.debug("all data has been deleted")
  }
}

// application runner
object ScalaApplication extends App {
  val log: Logger = LoggerFactory.getLogger(classOf[ScalaApplication])

  def debugContextBeans(): Unit = {
    val beanList = context.getBeanDefinitionNames.toList
    beanList.foreach(log.debug(_))
  }

  val context = SpringApplication.run(classOf[ScalaApplication], args: _*)

  // Only for debugging, remove when needed
  //debugContextBeans()

}

