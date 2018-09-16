package com.cmartin.learn.app

import com.cmartin.learn.controller.route.DummyController
import com.cmartin.learn.repository.impl.DummyRepositoryImpl
import com.cmartin.learn.service.impl.DummyServiceImpl

object MainApp extends App {

  val dummyRepository = new DummyRepositoryImpl()
  //val dummyService = new DummyServiceImpl(dummyRepository)
  //val dummyController = new DummyController(dummyService)

  println("MainApp message")
}
