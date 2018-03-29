package com.cmartin.research

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.boot.autoconfigure.SpringBootApplication
//import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories

//@EnableNeo4jRepositories
@SpringBootApplication
class ScalaApplication {
  val log: Logger = LoggerFactory.getLogger(classOf[ScalaApplication])

  def hello = log.debug("message from ScalaApplication")
}

