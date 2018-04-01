package com.cmartin.research

import java.time.LocalDate

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.{ApplicationRunner, SpringApplication}
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}

import scala.beans.BeanProperty

@Configuration
@ComponentScan
@EnableAutoConfiguration
class ScalaApplication {
  val log: Logger = LoggerFactory.getLogger(classOf[ScalaApplication])

  // bean declaration

  case class Aircraft(@BeanProperty id: Long,
                      @BeanProperty regNo: String,
                      @BeanProperty engineNo: Int,
                      @BeanProperty airlineName: String,
                      @BeanProperty deliverDate: LocalDate)


  class DependencyAnalyzer {
    def hello = s"scala class ${this.toString}"
  }

  @Configuration
  class MyConfiguration {
    @Bean def dependencyAnalyzer() = new DependencyAnalyzer()
  }


  @Bean
  def init(da: DependencyAnalyzer): ApplicationRunner = args => {
    log.debug("ScalaApplication/SpringBoot initialization")
    log.debug(s"message from a bean: ${da.hello}")
  }
}

// application runner
object ScalaApplication extends App {
  SpringApplication.run(classOf[ScalaApplication], args: _*)

  // Onlu for debugging, remove when needed
  /*
  val log: Logger = LoggerFactory.getLogger(classOf[ScalaApplication])
  val context = SpringApplication.run(classOf[ScalaApplication], args: _*)
  val beanList = context.getBeanDefinitionNames.toList
  beanList.foreach(log.debug(_))
  */

}
