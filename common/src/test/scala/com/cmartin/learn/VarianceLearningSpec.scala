package com.cmartin.learn

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import VarianceLearning.PalindromeManager
import VarianceLearning.PalindromeManager.StringOps

class VarianceLearningSpec
    extends AnyFlatSpec
    with Matchers {

  "Palindromes" should "check that a word is a palindrome" in {
    val word = "Reconocer"

    val result = word.isPalindrome

    result shouldBe true
  }

  it should "check that a sentence is a palindrome" in {
    val sentence = "Dábale arroz a la zorra el abad"

    val result = sentence.isPalindrome

    result shouldBe true
  }

  it should "check that a sentence is a multi line palindrome" in {
    val sentence: String =
      """Dábale 
         arroz
         a 
         la
         zorra 
         el 
         abad"""

    val result = sentence.isPalindrome

    result shouldBe true
  }

  it should "check that sentence list is a palindrome list" in {
    val sentences = List(
      "Dábale arroz a la zorra el abad",
      "Sé verlas al revés",
      "Somos o no somos",
      "Isaac no ronca así",
      "Amó la paloma",
      "Anita lava la tina",
      "Luz azul",
      "Yo hago yoga hoy",
      "Ana lava lana",
      "Ana",
      "Reconocer",
      "1331"
    )

    val results = sentences.map(_.isPalindrome)

    all(results) shouldBe true
  }

  it should "check that a sentence is not a palindrome" in {
    val sentence = "abc def xed cba"

    val result = sentence.isPalindrome

    result shouldBe false
  }

  "Sentences" should "find duplicate words in two senteces" in {
    val s1 = "The quick brown fox jumps over the lazy dog"
    val s2 = "The swift white fox chases the swift dog"

    val r = PalindromeManager.findDuplicateWords(s1, s2)
    info(s"diff: ${r}")

    r shouldBe Seq("The", "fox", "the", "dog")
  }

  it should "reverse the position of words in a sentence" in {
    val sentence = "Dábale arroz a la zorra el abad"

    val r = PalindromeManager.reverseWords(sentence)
    info(s"diff: ${r}")

    r shouldBe Seq("abad", "el", "zorra", "la", "a", "arroz", "Dábale")
  }

}
