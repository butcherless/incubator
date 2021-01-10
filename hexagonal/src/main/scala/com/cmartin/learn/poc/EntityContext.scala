package com.cmartin.learn.poc

import scala.language.experimental.macros

import io.getquill.NamingStrategy
import io.getquill.context.Context
import io.getquill.idiom.Idiom
import io.getquill.Insert

trait XXX_EntityContext[I <: Idiom, N <: NamingStrategy] {
  this: Context[I, N] =>

  def insertMacro[T](entity: T): Quoted[Insert[T]] = macro CommonOpsMacro.insert[T]
}
