import sbt.*

object Dependencies {

  // production code
  lazy val akkaHttp       = "com.typesafe.akka" %% "akka-http"            % Versions.akkaHttp
  lazy val akkaJson       = "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttp
  lazy val typesafeConfig = "com.typesafe"       % "config"               % Versions.config

  lazy val circeYaml = "io.circe" %% "circe-yaml" % Versions.circe

  // P O S T G R E S
  lazy val postgresDB = "org.postgresql"   % "postgresql"        % Versions.postgres
  lazy val neo4j      = "org.neo4j.driver" % "neo4j-java-driver" % Versions.neo4j

  lazy val playJson =
    "com.typesafe.play" %% "play-json" % Versions.playJson exclude ("com.fasterxml.jackson.core", "jackson-annotations")
  lazy val slf4j = "org.slf4j" % "slf4j-api" % Versions.slf4j
  lazy val slick =
    "com.typesafe.slick" %% "slick" % Versions.slick exclude ("org.slf4j", "slf4j-api") exclude ("com.typesafe", "config")
  lazy val slickPool     = "com.typesafe.slick" %% "slick-hikaricp"       % Versions.slick exclude ("org.slf4j", "slf4j-api")
  // quill
  lazy val quillJdbc     = "io.getquill"        %% "quill-jdbc"           % Versions.quill
  lazy val quillPostgres = "io.getquill"        %% "quill-async-postgres" % Versions.quillPostgres

  lazy val zio               = "dev.zio" %% "zio"                 % Versions.zio
  lazy val zioLogging        = "dev.zio" %% "zio-logging-slf4j"   % Versions.zioLogging
  lazy val zioPrelude        = "dev.zio" %% "zio-prelude"         % Versions.zioPrelude
  lazy val zioConfigLib      = "dev.zio" %% "zio-config"          % Versions.zioConfig
  lazy val zioConfigTypesafe = "dev.zio" %% "zio-config-typesafe" % Versions.zioConfig

  lazy val scalaReflect  = "org.scala-lang" % "scala-reflect"  % Versions.scalaVersion
  lazy val scalaCompiler = "org.scala-lang" % "scala-compiler" % Versions.scalaVersion

  // testing code
  lazy val akkaHttpTest = "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp  % "test"
  lazy val scalaTest    = "org.scalatest"     %% "scalatest"         % Versions.scalatest % "test"

  lazy val zioTestSbt = "dev.zio" %% "zio-test-sbt" % Versions.zio % "test"

  lazy val h2Database = "com.h2database" % "h2" % Versions.h2 % "test"

  lazy val zioTest = Seq(
    "dev.zio" %% "zio-test"          % Versions.zio % "test",
    "dev.zio" %% "zio-test-sbt"      % Versions.zio % "test",
    "dev.zio" %% "zio-test-magnolia" % Versions.zio % "test" // optional
  )

}
