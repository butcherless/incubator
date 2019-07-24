import Dependencies._
import sbt.Keys.libraryDependencies

lazy val projectVersion = "1.0.0-SNAPSHOT"

scalaVersion := "2.13.0"

lazy val commonSettings = Seq(
  organization := "com.cmartin.learn",
  version := projectVersion,
  scalaVersion := "2.13.0",
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
    libraryDependencies ++= Seq(slick, slickPool, logback, h2Database, scalaTest),
    parallelExecution in Test := false
  ).dependsOn(common, test)


lazy val service = (project in file("service"))
  .settings(
    commonSettings,
    name := "service",
    libraryDependencies ++= Seq(akkaActor, akkaHttp, akkaStream, typesafeConfig, json4sNative, playJson, scalaLogging, logback, sttp, scalaTest)
  ).dependsOn(common, repository)


lazy val controller = (project in file("controller"))
  .settings(
    commonSettings,
    name := "controller",
    libraryDependencies ++= Seq(scalaTest)
  ).dependsOn(common, service)


lazy val web = (project in file("web"))
  .settings(
    commonSettings,
    name := "web",
    libraryDependencies ++= Seq(scalaLogging, logback, scalaTest)
  ).dependsOn(common, controller, service)


lazy val test = (project in file("test"))
  .settings(
    commonSettings,
    name := "test"
  )


lazy val poc = (project in file("poc"))
  .settings(
    commonSettings,
    name := "poc",
    libraryDependencies ++= Seq(akkaHttp, akkaStream, scalaLogging, slick, slickPool, logback, h2Database, scalaTest),
    parallelExecution in Test := false
  ).dependsOn(test)


// quick research deps
lazy val sttp = "com.softwaremill.sttp" %% "core" % "1.6.3"
