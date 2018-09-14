package com.cmartin.learn.service

package object spec {

  trait DummyService {
    def operationOne(): String

    def searchKey(json: String, key: String): Unit // Option[String]
  }

}
