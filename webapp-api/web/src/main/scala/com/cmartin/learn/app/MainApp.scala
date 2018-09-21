package com.cmartin.learn.app

import com.cmartin.learn.repository.impl.DummyRepositoryImpl

object MainApp extends App {

  val dummyRepository = new DummyRepositoryImpl()
  //val dummyService = new DummyServiceImpl(dummyRepository)
  //val dummyController = new DummyController(dummyService)

  println("MainApp message")
}
