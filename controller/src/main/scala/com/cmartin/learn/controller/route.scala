package com.cmartin.learn.controller

import com.cmartin.learn.common.sayHello
import com.cmartin.learn.service.spec.DummyService

package object route {
  val dummyControllerVal = 0

  val myVal = sayHello()

  class DummyController(service: DummyService) {
    def create() = {
      service.operationOne()
    }
  }
}
