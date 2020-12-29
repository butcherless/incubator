package com.cmartin.learn

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.Locale

class PocSpec extends AnyFlatSpec with Matchers {

  behavior of "Poc spec"

  it should "print country iso codes" in {
    val codes = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2)

    info(s"code exists?: ${codes.contains("DE")}")
  }
}
