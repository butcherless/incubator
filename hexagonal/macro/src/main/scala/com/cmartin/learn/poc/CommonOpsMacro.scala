package com.cmartin.learn.poc

import scala.reflect.macros.whitebox.{Context => MacroContext}

class CommonOpsMacro(val c: MacroContext) {

  import c.universe._

  def insert[T](entity: Tree)(implicit t: WeakTypeTag[T]): Tree = {
    q"""
      import ${c.prefix}._
      quote {
        query[$t]
          .insert(lift($entity))
      }
     """
  }

  def update[T](entity: Tree)(implicit t: WeakTypeTag[T]): Tree = {
    q"""
      import ${c.prefix}._
      quote {
        query[$t]
          .filter(e => e.id == lift($entity.id))
          .update(lift($entity))
      }
     """
  }

  def delete[T](entity: Tree)(implicit t: WeakTypeTag[T]): Tree = {
    q"""
      import ${c.prefix}._
      quote {
        query[$t]
          .filter(e => e.id == lift($entity.id))
          .delete
      }
     """
  }

}
