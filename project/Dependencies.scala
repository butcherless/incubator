import sbt._

object Dependencies {

  // production code
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http"            % Versions.akkaHttp
  lazy val akkaJson = "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttp
  lazy val akkaActor =
    "com.typesafe.akka" %% "akka-actor" % Versions.akka exclude ("com.typesafe", "config")
  lazy val akkaTypedActor =
    "com.typesafe.akka" %% "akka-actor-typed" % Versions.akka exclude ("com.typesafe", "config")
  lazy val akkaStream =
    "com.typesafe.akka" %% "akka-stream" % Versions.akka exclude ("com.typesafe", "config")
  lazy val cats           = "org.typelevel" %% "cats-core"     % Versions.cats
  lazy val catsFree       = "org.typelevel" %% "cats-free"     % Versions.cats
  lazy val typesafeConfig = "com.typesafe"   % "config"        % Versions.config
  lazy val json4sNative   = "org.json4s"    %% "json4s-native" % Versions.json4s
  lazy val h2Database     = "com.h2database" % "h2"            % Versions.h2
  lazy val logback =
    "ch.qos.logback" % "logback-classic" % Versions.logback exclude ("org.slf4j", "slf4j-api")
  lazy val playJson =
    "com.typesafe.play" %% "play-json" % Versions.playJson exclude ("com.fasterxml.jackson.core", "jackson-annotations")
  lazy val scalaLogging =
    "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging exclude ("org.slf4j", "slf4j-api")
  lazy val scalaz = "org.scalaz" %% "scalaz-core" % Versions.scalaz
  lazy val slf4j  = "org.slf4j"   % "slf4j-api"   % Versions.slf4j
  lazy val slick =
    "com.typesafe.slick" %% "slick" % Versions.slick exclude ("org.slf4j", "slf4j-api") exclude ("com.typesafe", "config")
  lazy val slickPool =
    "com.typesafe.slick" %% "slick-hikaricp" % Versions.slick exclude ("org.slf4j", "slf4j-api")

  lazy val tapir          = "com.softwaremill.sttp.tapir" %% "tapir-core"             % Versions.tapir
  lazy val tapirAkkaHttp  = "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % Versions.tapir
  lazy val tapirJsonCirce = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"       % Versions.tapir
  lazy val tapirOpenApi   = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"     % Versions.tapir
  lazy val tapirOpenApiYaml =
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Versions.tapir
  lazy val swaggerUiAkka =
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % Versions.tapir

  lazy val zio = "dev.zio" %% "zio" % Versions.zio

  // testing code
  lazy val akkaHttpTest = "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp  % "test"
  lazy val scalaTest    = "org.scalatest"     %% "scalatest"         % Versions.scalatest % "test"
  lazy val uTest        = "com.lihaoyi"       %% "utest"             % Versions.utest     % "test"

  lazy val zioTest    = "dev.zio" %% "zio-test"     % Versions.zio % "test"
  lazy val zioTestSbt = "dev.zio" %% "zio-test-sbt" % Versions.zio % "test"
}
