package com.cmartin.research

import com.cmartin.repository.PersonRepository
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.{ApplicationRunner, SpringApplication}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableNeo4jRepositories(Array("com.cmartin.repository"))
@SpringBootApplication
class ScalaApplication {
  val log: Logger = LoggerFactory.getLogger(classOf[ScalaApplication])




  @Configuration
  class AppConfig(repo: PersonRepository){

    @Bean def dependencyAnalyzer() = new DependencyAnalyzer(repo)
  }

  @Configuration
  @EnableTransactionManagement
  class MyConfiguration {


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


  //@Bean
  def init(da: DependencyAnalyzer): ApplicationRunner = args => {
    log.debug(s"dependency analyzer: ${da.analyze}")

    log.debug(s"person count: ${da.personCount}")
  }

  /*
    //@Bean
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
  */
}

// application runner
object ScalaApplication extends App {
  val log: Logger = LoggerFactory.getLogger(classOf[ScalaApplication])

  def debugContextBeans(): Unit = {
    val beanList = context.getBeanDefinitionNames.toList
    beanList.foreach(log.debug(_))
  }

  val context = SpringApplication.run(classOf[ScalaApplication], args: _*)

  val da = context.getBean(classOf[DependencyAnalyzer])
  log.debug(s"dependency analyzer: ${da.analyze}")
  log.debug(s"person count: ${da.personCount}")
  // Only for debugging, remove when needed
  //debugContextBeans()

}

class DependencyAnalyzer(repo: PersonRepository) {
  def analyze = s"analyzing dependencies"

  def personCount = repo.count()
}