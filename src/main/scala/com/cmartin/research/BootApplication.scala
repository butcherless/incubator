package com.cmartin.research

import org.springframework.boot.SpringApplication

object BootApplication extends App {
  val context = SpringApplication.run(classOf[ScalaApplication])

  val scalaApplication = context.getBean(classOf[ScalaApplication])
  scalaApplication.hello
}