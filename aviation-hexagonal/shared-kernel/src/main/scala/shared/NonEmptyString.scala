package shared

opaque type NonEmptyString = String

object NonEmptyString {
  def from(value: String): Either[String, NonEmptyString] =
    if (value.trim.nonEmpty) Right(value.trim)
    else Left("Value must not be blank")

  def unsafeFrom(value: String): NonEmptyString =
    from(value).fold(msg => throw new IllegalArgumentException(msg), identity)

  extension (s: NonEmptyString) def value: String = s
}
