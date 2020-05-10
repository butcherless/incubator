import sbt._

object Dependencies {
  lazy val akkaHttpVersion     = "10.1.11"
  lazy val akkaVersion         = "2.6.5"
  lazy val catsVersion         = "2.0.0"
  lazy val configVersion       = "1.4.0"
  lazy val h2Version           = "1.4.200"
  lazy val json4sVersion       = "3.6.8"
  lazy val logbackVersion      = "1.2.3"
  lazy val playJsonVersion     = "2.8.1"
  lazy val scalaLoggingVersion = "3.9.2"
  lazy val scalazVersion       = "7.2.27"
  lazy val scalatestVersion    = "3.1.2"
  lazy val slf4jVersion        = "1.7.30"
  lazy val slickVersion        = "3.3.2"
  lazy val sttpVersion         = "1.7.2"
  lazy val utestVersion        = "0.6.6"
  lazy val tapirVersion        = "0.14.5"
  lazy val zioVersion          = "1.0.0-RC18-2"


  // production code
  lazy val akkaHttp       = "com.typesafe.akka"          %% "akka-http"            % akkaHttpVersion
  lazy val akkaJson       = "com.typesafe.akka"          %% "akka-http-spray-json" % akkaHttpVersion
  lazy val akkaActor      = "com.typesafe.akka"          %% "akka-actor"           % akkaVersion exclude ("com.typesafe", "config")
  lazy val akkaTypedActor = "com.typesafe.akka"          %% "akka-actor-typed"     % akkaVersion exclude ("com.typesafe", "config")
  lazy val akkaStream     = "com.typesafe.akka"          %% "akka-stream"          % akkaVersion exclude ("com.typesafe", "config")
  lazy val cats           = "org.typelevel"              %% "cats-core"            % catsVersion
  lazy val catsFree       = "org.typelevel"              %% "cats-free"            % catsVersion
  lazy val typesafeConfig = "com.typesafe"               % "config"                % configVersion
  lazy val json4sNative   = "org.json4s"                 %% "json4s-native"        % json4sVersion
  lazy val h2Database     = "com.h2database"             % "h2"                    % h2Version
  lazy val logback        = "ch.qos.logback"             % "logback-classic"       % logbackVersion exclude ("org.slf4j", "slf4j-api")
  lazy val playJson       = "com.typesafe.play"          %% "play-json"            % playJsonVersion exclude ("com.fasterxml.jackson.core", "jackson-annotations")
  lazy val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"        % scalaLoggingVersion exclude ("org.slf4j", "slf4j-api")
  lazy val scalaz         = "org.scalaz"                 %% "scalaz-core"          % scalazVersion
  lazy val slf4j          = "org.slf4j"                  % "slf4j-api"             % slf4jVersion
  lazy val slick          = "com.typesafe.slick"         %% "slick"                % slickVersion exclude ("org.slf4j", "slf4j-api") exclude ("com.typesafe", "config")
  lazy val slickPool      = "com.typesafe.slick"         %% "slick-hikaricp"       % slickVersion exclude ("org.slf4j", "slf4j-api")
  lazy val sttp           = "com.softwaremill.sttp"      %% "core"                 % sttpVersion

  lazy val tapir            = "com.softwaremill.sttp.tapir" %% "tapir-core"                 % tapirVersion
  lazy val tapirAkkaHttp    = "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server"     % tapirVersion
  lazy val tapirJsonCirce   = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"           % tapirVersion
  lazy val tapirOpenApi     = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"         % tapirVersion
  lazy val tapirOpenApiYaml = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml"   % tapirVersion
  lazy val swaggerUiAkka    = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % tapirVersion

  lazy val zio              = "dev.zio" %% "zio" % zioVersion

  // testing code
  lazy val akkaHttpTest = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion  % "test"
  lazy val scalaTest    = "org.scalatest"     %% "scalatest"         % scalatestVersion % "test"
  lazy val uTest        = "com.lihaoyi"       %% "utest"             % utestVersion     % "test"

  lazy val zioTest      = "dev.zio"           %% "zio-test"          % zioVersion       % "test"
  lazy val zioTestSbt   = "dev.zio"           %% "zio-test-sbt"      % zioVersion       % "test"
}
