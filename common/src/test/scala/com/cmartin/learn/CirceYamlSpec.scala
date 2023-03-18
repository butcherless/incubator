package com.cmartin.learn

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.circe.yaml.parser
import io.circe.{ Json, ParsingFailure }

class CirceYamlSpec
    extends AnyFlatSpec
    with Matchers {

  behavior of "CirceYaml"

  val yamlOne = """
  id1:
    name: project1
    desc: proyect one
  id2:
    name: project2
    desc: proyect two
  """.stripMargin

  val yamlTwo = """
  id2:
    name: project2
    desc: proyect two
  id1:
    name: project1
    desc: proyect one
  """.stripMargin

  it should "parse a yaml string" in {
    val yamlOneEither: Either[ParsingFailure, Json] = parser.parse(yamlOne)

    yamlOneEither.isRight shouldBe true
    yamlOneEither map { y =>
      info(s"yamlOne: $y")
    }

    val yamlTwoEither: Either[ParsingFailure, Json] = parser.parse(yamlTwo)

    yamlTwoEither.isRight shouldBe true
    yamlTwoEither map { y =>
      info(s"yamlTwo: $y")
    }

    val result = for {
      y1  <- yamlOneEither
      y2  <- yamlTwoEither
      res <- Either.cond(y1 == y2, "YES", "NO")
    } yield res

    result shouldBe Right("YES")
  }

}
