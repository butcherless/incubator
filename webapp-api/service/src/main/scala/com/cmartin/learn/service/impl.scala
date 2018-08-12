package com.cmartin.learn.service

import com.cmartin.learn.common.sayHello
import com.cmartin.learn.repository.spec.DummyRepository
import com.cmartin.learn.service.spec.DummyService

package object impl {

  class DummyServiceImpl(repository: DummyRepository) extends DummyService {
    override def operationOne(): String = {
      repository.saveDummy()
      sayHello()
    }
  }

}
