import Dependencies._

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / organization := "com.cmartin.learn"

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(scalaTest),
  scalacOptions ++= Seq( // some of the Rob Norris tpolecat options
    "-deprecation",      // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8",                 // Specify character encoding used by source files.
    "-explaintypes",         // Explain type errors in more detail.
    "-explaintypes",         // Explain type errors in more detail.
    "-unchecked",            // Enable additional warnings where generated code depends on assumptions.
    "-feature",              // Emit warning and location for usages of features that should be imported explicitly.
    "-language:higherKinds", // Allow higher-kinded types
    "-language:implicitConversions",
    "-language:postfixOps",
    "-language:reflectiveCalls"
  ),
  test in assembly := {}
)

lazy val common = (project in file("common"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    //Defaults.itSettings,
    name := "common"
  )

lazy val repository = (project in file("repository"))
  .configs(IntegrationTest extend Test)
  .settings(
    commonSettings,
    Defaults.itSettings,
    name := "repository",
    libraryDependencies ++= Seq(
      slick,
      slickPool,
      typesafeConfig,
      logback,
      slf4j,
      h2Database,
      postgresDB
    ),
    parallelExecution in Test := false
  )
  .dependsOn(common, testUtils)

lazy val service = (project in file("service"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    name := "service",
    libraryDependencies ++= Seq(typesafeConfig, json4sNative, playJson, scalaLogging, logback)
  )
  .dependsOn(common, repository)

lazy val controller = (project in file("controller"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    name := "controller"
  )
  .dependsOn(common, service)

lazy val testUtils = (project in file("test"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    name := "test-utils"
  )

lazy val pocZioZLayer = (project in file("poc-zio-zlayer"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    name := "poc-zio-zlayer",
    libraryDependencies ++= Seq(zio, zioTest, zioTestSbt),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )

lazy val quillMacros = project
  .in(file("hexagonal/macro"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    name := "quillMacros",
    libraryDependencies ++= Seq(quillJdbc, quillPostgres, scalaTest, scalaReflect, scalaCompiler)
  )

lazy val hexagonal = (project in file("hexagonal"))
  .configs(IntegrationTest extend Test)
  .settings(
    commonSettings,
    Defaults.itSettings,
    name := "hexagonal",
    libraryDependencies ++= Seq(
      logback,
      quillJdbc,
      quillPostgres,
      postgresDB,
        slick,
      slickPool,
      typesafeConfig,
      zioPrelude,
      h2Database,
      scalaTest
    ),
    parallelExecution in Test := false
  )
  .dependsOn(quillMacros,testUtils)

addCommandAlias("xcoverage", "clean;coverage;test;coverageReport")
addCommandAlias("xreload", "clean;reload")
addCommandAlias("xstart", "clean;reStart")
addCommandAlias("xstop", "reStop;clean")
addCommandAlias("xupdate", "clean;update")
addCommandAlias("xdup", "dependencyUpdates")