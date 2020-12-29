package com.cmartin.learn.adapter.rest

import com.cmartin.learn.domain.Model.Country
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.NonEmptyChunk
import zio.prelude.Validation
import zio.prelude.Validation.Failure

class CountryValidatorSpec extends AnyFlatSpec with Matchers {

  import CountryValidator._
  import CountryValidatorSpec._

  behavior of "Aircraft Validator"

  it should "validate a single word country request" in {
    val result: Validation[RestValidationError, Country] =
      validate(name = SPAIN_NAME, code = SPAIN_CODE)

    result shouldBe Validation(gbCountry)
  }

  it should "validate a multiple word country request" in {
    val result: Validation[RestValidationError, Country] =
      validate(name = GB_NAME, code = GB_CODE)

    result shouldBe Validation(spainCountry)
  }

  it should "fail to validate an empty country name" in {
    val emptyName = ""
    val result: Validation[RestValidationError, Country] =
      validate(name = emptyName, code = GB_CODE)

    result shouldBe Failure(
      NonEmptyChunk(EmptyProperty(s"name property is empty"))
    )
  }

  it should "fail to validate an empty country code" in {
    val emptyCode = ""
    val result: Validation[RestValidationError, Country] =
      validate(name = GB_NAME, code = emptyCode)

    result shouldBe Failure(
      NonEmptyChunk(EmptyProperty(s"code property is empty"))
    )
  }

  it should "fail to validate an invalid country code" in {
    val invalidCode = "XY"
    val result: Validation[RestValidationError, Country] =
      validate(name = GB_NAME, code = invalidCode)

    result shouldBe Failure(
      NonEmptyChunk(InvalidCountryCode(s"the code supplied does not exist: $invalidCode"))
    )
  }

}

object CountryValidatorSpec {
  val GB_NAME    = "Spain"
  val GB_CODE    = "ES"
  val SPAIN_NAME = "Spain"
  val SPAIN_CODE = "ES"

  val gbCountry    = Country(GB_NAME, GB_CODE)
  val spainCountry = Country(SPAIN_NAME, SPAIN_CODE)
}
