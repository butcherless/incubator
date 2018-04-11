package com.cmartin.research

import com.cmartin.domain.AircraftBean
import com.cmartin.repository.AircraftRepository
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
  class ApplicationConfiguration(repo: AircraftRepository) {

    @Bean def dependencyAnalyzer() = new DependencyAnalyzer(repo)
  }


  @Bean def init(repo: AircraftRepository): ApplicationRunner = args => {
    log.debug("init: delete all beans")
    repo.deleteAll();

    List(
      AircraftBean(regNo = "ec-mmx", engineNo = 2, name = "Sierra Nevada", deliverDate = getLocalDate(2018, 2, 15)),
      AircraftBean(regNo = "ec-lvl", engineNo = 2, name = "Aneto", deliverDate = getLocalDate(2012, 5, 1)),
      AircraftBean(regNo = "ec-lxr", engineNo = 2, name = "Pico de las Nieves", deliverDate = getLocalDate(2015, 9, 1)),
      AircraftBean(regNo = "ec-nop", engineNo = 2, name = "Teide", deliverDate = getLocalDate(2014, 12, 1)),
      AircraftBean(regNo = "ec-raw", engineNo = 2, name = "Mulhacen", deliverDate = getLocalDate(2018, 3, 17))
    ).foreach(repo.save(_))
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
  context.registerShutdownHook()
  context.close()
}

/** main class with spring boot callback function 'run'
  *
  * @param repo
  */
class DependencyAnalyzer(repo: AircraftRepository) extends CommandLineRunner {
  def analyze = s"analyzing dependencies"

  def personCount = repo.count()

  override def run(args: String*): Unit = {
    log.debug("command line runner callback function")
    log.debug(s"repo count: ${personCount}")
  }
}