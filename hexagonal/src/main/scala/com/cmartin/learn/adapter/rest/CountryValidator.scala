package com.cmartin.learn.adapter.rest

import com.cmartin.learn.domain.Model.Country
import zio.prelude.Validation

import java.util.Locale
import scala.util.matching.Regex

object CountryValidator {

  private val countryCodes              = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2)
  private val countryNamePattern: Regex = raw"[a-zA-Z ]+".r

  def validate(name: String, code: String): Validation[RestValidationError, Country] = {
    Validation.validateWith(
      validateName(name),
      validateCode(code)
    )(Country)
  }

  def validateName(name: String): Validation[RestValidationError, String] =
    for {
      nonEmpty <- validateEmptyText(name, EmptyProperty(s"name property is empty"))
      result   <- validateNameChars(nonEmpty)
    } yield result

  def validateNameChars(name: String): Validation[RestValidationError, String] = {
    Validation
      .fromPredicateWith(
        InvalidNameCharacters(s"invalid characters for name: $name")
      )(name)(countryNamePattern.matches)
  }

  def validateCode(code: String): Validation[RestValidationError, String] =
    for {
      nonEmpty <- validateEmptyText(code, EmptyProperty(s"code property is empty"))
      result   <- validateCountryCode(nonEmpty)
    } yield result

  def validateCountryCode(code: String): Validation[RestValidationError, String] = {
    Validation
      .fromPredicateWith(
        InvalidCountryCode(s"the code supplied does not exist: $code")
      )(code)(countryCodes.contains)
  }

  private def validateEmptyText(
      text: String,
      error: RestValidationError
  ): Validation[RestValidationError, String] = {
    Validation
      .fromPredicateWith(error)(text)(_.nonEmpty)
  }

  sealed trait RestValidationError

  abstract class DescribedError extends RestValidationError {
    val description: String
  }

  case class InvalidCountryCode(description: String) extends DescribedError

  case class EmptyProperty(description: String) extends DescribedError

  case class InvalidNameCharacters(description: String) extends DescribedError

}
