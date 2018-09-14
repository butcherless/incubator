package com.cmartin.learn.repository

import com.cmartin.learn.common.getDummyInt
import com.cmartin.learn.repository.spec.DummyRepository

package object impl {

  class DummyRepositoryImpl extends DummyRepository {
    override def saveDummy(): Int = getDummyInt()
  }

  object DummyRepositoryImpl {
    def apply(): DummyRepositoryImpl = new DummyRepositoryImpl()
  }

}
