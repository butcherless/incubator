package com.cmartin.research

import java.time.LocalDateTime

import com.cmartin.domain.{AircraftBean, Airline, Flight, Route}
import com.cmartin.repository.Repos
import com.cmartin.research.DataManager.getLocalDate
import com.cmartin.research.ScalaApplication.log
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.{ApplicationRunner, CommandLineRunner, SpringApplication}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableNeo4jRepositories(Array("com.cmartin.repository"))
@SpringBootApplication
class ScalaApplication {

  //val log: Logger = LoggerFactory.getLogger(classOf[ScalaApplication])

  @Configuration
  @EnableTransactionManagement
  class Neo4jConfiguration {

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


  @Configuration
  class ApplicationConfiguration() {
    @Bean def repos = new Repos()

    @Bean def dependencyAnalyzer() = new DependencyAnalyzer(repos)
  }


  @Bean def init(repos: Repos): ApplicationRunner = args => {
    log.debug("init: delete all beans")
    repos.alRepo.deleteAll()
    repos.acRepo.deleteAll();

    val airline = Airline("Iberia", "ES")
    repos.alRepo.save(airline)

    List(
      AircraftBean("ec-mmx", 2, "Sierra Nevada", getLocalDate(2018, 2, 15), airline),
      AircraftBean("ec-lvl", 2, "Aneto", getLocalDate(2012, 5, 1), airline),
      AircraftBean("ec-lxr", 2, "Pico de las Nieves", getLocalDate(2015, 9, 1), airline),
      AircraftBean("ec-nop", 2, "Teide", getLocalDate(2014, 12, 1), airline),
      AircraftBean("ec-raw", 2, "Mulhacen", getLocalDate(2018, 3, 17), airline)
    ).foreach(repos.acRepo.save(_))

    List(
      Route("MAD","TFN"),
      Route("TFN", "MAD"),
      Route("MAD", "LPA"),
      Route("LPA", "MAD")
    ).foreach(repos.roRepo.save(_))

    List(
      Flight("ux9117",LocalDateTime.now,LocalDateTime.now,AircraftBean("ec-mmx", 2, "Sierra Nevada", getLocalDate(2018, 2, 15), airline))
    ).foreach(repos.flRepo.save(_))
  }

}

// application runner
object ScalaApplication extends App {
  val log: Logger = LoggerFactory.getLogger(classOf[ScalaApplication])

  /*
  def debugContextBeans(): Unit = {
    val beanList = context.getBeanDefinitionNames.toList
    beanList.foreach(log.debug(_))
  }
  */

  val context = SpringApplication.run(classOf[ScalaApplication], args: _*).close

}

/** main class with spring boot callback function 'run'
  *
  * @param repos
  */
class DependencyAnalyzer(repos: Repos) extends CommandLineRunner {
  def analyze = s"analyzing dependencies"

  def personCount = repos.acRepo.count

  def airlineCount = repos.alRepo.count

  override def run(args: String*): Unit = {
    log.debug("command line runner callback function")
    log.debug(s"aircraft repo count: ${personCount}")
    log.debug(s"airline repo count: ${airlineCount}")
  }
}