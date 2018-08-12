package com.cmartin.learn.repository

import com.cmartin.learn.repository.spec.DummyRepository
import com.cmartin.learn.common.getDummyInt

package object impl {

  class DummyRepositoryImpl extends DummyRepository {
    override def saveDummy(): Int = getDummyInt()
  }

}
