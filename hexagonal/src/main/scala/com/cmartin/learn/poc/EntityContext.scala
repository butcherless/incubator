package com.cmartin.learn.poc

import scala.language.experimental.macros
import io.getquill.idiom.Idiom
import io.getquill.NamingStrategy
import io.getquill.context.Context

trait EntityContext[I <: Idiom, N <: NamingStrategy] { this: Context[I, N] =>
  //def insert[T](entity: T): Unit = macro InsertMacro.insert[T]
}
