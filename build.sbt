import Dependencies._

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / organization := "com.cmartin.learn"

lazy val commonSettings = Seq(
  resolvers += Resolver.sonatypeRepo("snapshot"),
  libraryDependencies ++= Seq(scalaTest),
  scalacOptions ++= Seq(     // some of the Rob Norris tpolecat options
    "-deprecation",          // Emit warning and location for usages of deprecated APIs.
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
  )
)

lazy val common = (project in file("common"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.1",
    Defaults.itSettings,
    // Defaults.itSettings,
    name := "common"
  )

lazy val repository = (project in file("repository"))
  .configs(IntegrationTest extend Test)
  .settings(
    commonSettings,
    Defaults.itSettings,
    name              := "repository",
    libraryDependencies ++= Seq(
      slick,
      slickPool,
      typesafeConfig,
      logback,
      slf4j,
      h2Database,
      postgresDB
    ),
    parallelExecution := false
  )
  .dependsOn(common, testUtils)

lazy val testUtils = (project in file("test"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    name := "test-utils"
  )

lazy val quillMacros = project
  .in(file("hexagonal/macro"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    name := "quillMacros",
    libraryDependencies ++= Seq(quillJdbc, quillPostgres, scalaTest, scalaReflect, scalaCompiler),
    assemblyStrategy
  )

lazy val hexagonal = (project in file("hexagonal"))
  .configs(IntegrationTest extend Test)
  .settings(
    commonSettings,
    Defaults.itSettings,
    name              := "hexagonal",
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
    parallelExecution := false,
    assemblyStrategy
  )
  .dependsOn(quillMacros, testUtils)

lazy val assemblyStrategy = ThisBuild / assemblyMergeStrategy := {
  case "module-info.class"                                    => MergeStrategy.last
  case "META-INF/io.netty.versions.properties"                => MergeStrategy.last
  case "META-INF/maven/org.webjars/swagger-ui/pom.properties" => MergeStrategy.first
  case x                                                      =>
    val oldStrategy = assemblyMergeStrategy.value
    oldStrategy(x)
}

Global / onChangedBuildSource := ReloadOnSourceChanges

addCommandAlias("xcoverage", "clean;coverage;test;coverageReport")
addCommandAlias("xreload", "clean;reload")
addCommandAlias("xstart", "clean;reStart")
addCommandAlias("xstop", "reStop;clean")
addCommandAlias("xupdate", "clean;update")
addCommandAlias("xdup", "dependencyUpdates")

// clear screen and banner
lazy val cls = taskKey[Unit]("Prints a separator")
cls := {
  val brs     = "\n".repeat(2)
  val message = "* B U I L D   B E G I N S   H E R E *"
  val chars   = "*".repeat(message.length())
  println(s"$brs$chars")
  println("* B U I L D   B E G I N S   H E R E *")
  println(s"$chars$brs ")
}
