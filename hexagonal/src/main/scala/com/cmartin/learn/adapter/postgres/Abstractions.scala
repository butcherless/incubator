package com.cmartin.learn.adapter.postgres

import com.cmartin.learn.adapter.postgres.Model.CountryDbo
import io.getquill.NamingStrategy
import io.getquill.context.Context
import io.getquill.idiom.Idiom

object Abstractions {

  trait CountryContext[I <: Idiom, N <: NamingStrategy] {
    this: Context[I, N] =>

    def findByCode_(code: String) =
      quote {
        query[CountryDbo].filter(c => c.code == lift(code))
      }

    def insert_(dbo: CountryDbo) =
      quote {
        query[CountryDbo].insert(lift(dbo))
      }
  }

}
