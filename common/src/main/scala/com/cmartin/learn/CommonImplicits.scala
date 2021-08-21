package com.cmartin.learn

object CommonImplicits {

  implicit class Tuple2Enhancements[L, R](tuple2: (L, R)) {
    def left: L = tuple2._1

    def right: R = tuple2._2
  }

}
