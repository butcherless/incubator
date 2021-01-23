package com.cmartin.learn.poc

import io.getquill.{Insert, NamingStrategy}
import io.getquill.context.Context
import io.getquill.idiom.Idiom

import scala.language.experimental.macros

trait XXX_EntityContext[I <: Idiom, N <: NamingStrategy] {
  this: Context[I, N] =>

  def insertMacro[T](entity: T): Quoted[Insert[T]] = macro CommonOpsMacro.insert[T]
}
