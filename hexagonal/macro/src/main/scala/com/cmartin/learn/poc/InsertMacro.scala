package com.cmartin.learn.poc

import scala.reflect.macros.whitebox.{Context => MacroContext}
class InsertMacro(val c: MacroContext) {

  import c.universe._

  def insert[T](entity: Tree)(implicit t: WeakTypeTag[T]): Tree = {
    q"""
        import ${c.prefix}._
        val insertQuery = quote {
          query[$t].insert(lift($entity))
        }
        run(insertQuery)
        ()
      """
  }
}
