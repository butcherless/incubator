import Dependencies._

lazy val projectVersion = "1.0.0-SNAPSHOT"

scalaVersion := "2.13.2"

lazy val commonSettings = Seq(
  organization := "com.cmartin.learn",
  version := projectVersion,
  scalaVersion := "2.13.2",
  libraryDependencies ++= Seq(scalaTest),
  scalacOptions ++= Seq( // some of the Rob Norris tpolecat options
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-explaintypes",                     // Explain type errors in more detail.
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:implicitConversions",
    "-language:postfixOps",
    "-language:reflectiveCalls"
  ),
  test in assembly := {}

)

lazy val common = (project in file("common"))
  .settings(
    commonSettings,
    name := "common",
    libraryDependencies ++= Seq(scalaTest)
  )

lazy val repository = (project in file("repository"))
  .settings(
    commonSettings,
    name := "repository",
    libraryDependencies ++= Seq(slick, slickPool, typesafeConfig, logback, slf4j, h2Database),
    parallelExecution in Test := false
  ).dependsOn(common, testUtils)


lazy val service = (project in file("service"))
  .settings(
    commonSettings,
    name := "service",
    libraryDependencies ++= Seq(typesafeConfig, json4sNative, playJson, scalaLogging, logback, sttp)
  ).dependsOn(common, repository)


lazy val controller = (project in file("controller"))
  .settings(
    commonSettings,
    name := "controller",
  ).dependsOn(common, service)


lazy val web = (project in file("web"))
  .settings(
    commonSettings,
    name := "web",
    libraryDependencies ++= Seq(
      scalaLogging, logback, akkaHttp, akkaTypedActor, akkaStream,
      tapir, tapirAkkaHttp, tapirJsonCirce, tapirOpenApi, tapirOpenApiYaml, swaggerUiAkka)
  ).dependsOn(common, controller, service)


lazy val testUtils = (project in file("test"))
  .settings(
    commonSettings,
    name := "test-utils"
  )


lazy val poc = (project in file("poc"))
  .settings(
    commonSettings,
    name := "poc",
    libraryDependencies ++= Seq(akkaHttp, akkaStream, scalaLogging, slick, slickPool, logback, slf4j, h2Database),
    parallelExecution in Test := false
  ).dependsOn(testUtils)

lazy val pocZioZLayer = (project in file("poc-zio-zlayer"))
  .settings(
    commonSettings,
    name := "poc-zio-zlayer",
    libraryDependencies ++= Seq(zio,zioTest,zioTestSbt),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )