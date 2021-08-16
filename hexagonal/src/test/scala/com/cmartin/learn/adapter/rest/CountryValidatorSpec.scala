package com.cmartin.learn.adapter.rest

import com.cmartin.learn.domain.Model.Country
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.NonEmptyChunk

class CountryValidatorSpec /*                                            */ extends AnyFlatSpec with Matchers {

  import CountryValidator._
  import CountryValidatorSpec._

  behavior of "Aircraft Validator"

  it should "validate a single word country request" in {
    val result: Either[NonEmptyChunk[RestValidationError], Country] =
      validate(name = SPAIN_NAME, code = SPAIN_CODE).toEither

    result shouldBe Right(gbCountry)
  }

  it should "validate a multiple word country request" in {
    val result = validate(name = GB_NAME, code = GB_CODE).toEither

    result shouldBe Right(spainCountry)
  }

  it should "fail to validate an empty country name" in {
    val emptyName = ""
    val result    = validate(name = emptyName, code = GB_CODE).toEither

    result shouldBe Left(NonEmptyChunk(EmptyProperty(s"name property is empty")))
  }

  it should "fail to validate an empty country code" in {
    val emptyCode = ""
    val result    = validate(name = GB_NAME, code = emptyCode).toEither

    result shouldBe Left(NonEmptyChunk(EmptyProperty(s"code property is empty")))
  }

  it should "fail to validate an invalid country code" in {
    val invalidCode = "XY"
    val result      = validate(name = GB_NAME, code = invalidCode).toEither

    result shouldBe Left(
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
