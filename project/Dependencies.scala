import sbt._

object Dependencies {
  lazy val akkaHttpVersion     = "10.1.9"
  lazy val akkaVersion         = "2.5.23"
  lazy val catsVersion         = "2.0.0-M4"
  lazy val configVersion       = "1.3.4"
  lazy val h2Version           = "1.4.199"
  lazy val json4sVersion       = "3.6.7"
  lazy val logbackVersion      = "1.2.3"
  lazy val playJsonVersion     = "2.7.4"
  lazy val scalaLoggingVersion = "3.9.2"
  lazy val scalazVersion       = "7.2.27"
  lazy val scalatestVersion    = "3.0.8"
  lazy val slf4jVersion        = "1.7.25"
  lazy val slickVersion        = "3.3.2"
  lazy val specs2Version       = "4.5.1"
  lazy val utestVersion        = "0.6.6"

  
  // production code
  lazy val akkaHttp       = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  lazy val akkaJson       = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  lazy val akkaActor      = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  lazy val akkaStream     = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  lazy val cats           = "org.typelevel" %% "cats-core" % catsVersion
  lazy val catsFree       = "org.typelevel" %% "cats-free" % catsVersion
  lazy val typesafeConfig = "com.typesafe" % "config" % configVersion
  lazy val json4sNative   = "org.json4s" %% "json4s-native" % json4sVersion
  lazy val h2Database     = "com.h2database" % "h2" % h2Version
  lazy val logback        = "ch.qos.logback" % "logback-classic" % logbackVersion
  lazy val playJson       = "com.typesafe.play" % "play-json_2.12" % playJsonVersion
  lazy val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  lazy val scalaz         = "org.scalaz" %% "scalaz-core" % scalazVersion
  lazy val slf4j          = "org.slf4j" %% "slf4j-nop" % slf4jVersion
  lazy val slick          = "com.typesafe.slick" %% "slick" % slickVersion
  lazy val slickPool      = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion

  
  // testing code
  lazy val akkaHttpTest = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test"
  lazy val scalaTest    = "org.scalatest" %% "scalatest" % scalatestVersion % "test"
  lazy val specs2       = "org.specs2" %% "specs2-core" % specs2Version % "test"
  lazy val uTest        = "com.lihaoyi" %% "utest" % utestVersion % "test"

}
