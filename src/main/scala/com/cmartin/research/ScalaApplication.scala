package com.cmartin.research

import java.time.LocalDate

import com.cmartin.domain.{AircraftBean, Person}
import com.cmartin.repository.{AircraftRepository, PersonRepository}
import com.cmartin.research.DataManager.getLocalDate
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.{ApplicationRunner, SpringApplication}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager
import org.springframework.transaction.annotation.{EnableTransactionManagement, Transactional}

//@Configuration
//@ComponentScan
//@EnableAutoConfiguration
@EnableNeo4jRepositories(Array("com.cmartin.repository"))
@SpringBootApplication
class ScalaApplication {
  val log: Logger = LoggerFactory.getLogger(classOf[ScalaApplication])

  class DependencyAnalyzer {
    def hello = s"scala class ${this.toString}"
  }

  //@NodeEntity
  //class Person(@BeanProperty @Id @GeneratedValue id: java.lang.Long, @BeanProperty name: String)


  @Configuration
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
  @Transactional
  //def init(da: DependencyAnalyzer): ApplicationRunner = args => {
  def init(personRepo: PersonRepository, aircraftRepo: AircraftRepository): ApplicationRunner = args => {
    log.debug("ScalaApplication/SpringBoot initialization")
    log.debug(s"person entity count: ${personRepo.count}")
    //DataManager.loadData
    val p = new Person("pepe")
    personRepo.save(p)
    log.debug(s"person entity count: ${personRepo.count}")


    val ac = AircraftBean(regNo = "ec-lxr", engineNo = 2, name = "Pico de las Nieves", deliverDate = getLocalDate(2015, 9, 1))
    aircraftRepo.save(ac)
    log.debug(s"aircraft entity count: ${aircraftRepo.count}")

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

