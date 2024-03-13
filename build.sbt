import Dependencies.*

ThisBuild / scalaVersion := "2.13.13"
ThisBuild / organization := "com.cmartin.learn"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val commonSettings = Seq(
  // resolvers += Resolver.sonatypeRepo("snapshot"),
  scalacOptions ++= Seq(     // some of the Rob Norris tpolecat options
    "-deprecation",          // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8",                 // Specify character encoding used by source files.
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
  .settings(
    libraryDependencies ++= Seq(circeYaml, scalaTest) ++ zioTest,
    name := "common",
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

lazy val repository = (project in file("repository"))
  .settings(
    commonSettings,
    name              := "repository",
    libraryDependencies ++= Seq(
      slick,
      slickPool,
      typesafeConfig,
      // logback,
      slf4j,
      h2Database,
      postgresDB,
      scalaTest
    ),
    parallelExecution := false
  )
  .dependsOn(common, testUtils)

lazy val testUtils = (project in file("test"))
  .settings(
    commonSettings,
    name := "test-utils"
  )

lazy val quillMacros = project
  .in(file("hexagonal/macro"))
  .settings(
    commonSettings,
    name := "quillMacros",
    libraryDependencies ++= Seq(quillJdbc, quillPostgres, scalaTest, scalaReflect, scalaCompiler),
    assemblyStrategy
  )

lazy val neo4jRepository = project
  .in(file("neo4j-repository"))
  .settings(
    commonSettings,
    name                 := "neo4j-repository",
    libraryDependencies ++= Seq(
      zio,
      zioLogging,
      // logback,
      neo4j
    ),
    parallelExecution    := false,
    Compile / run / fork := true
  )

lazy val hexagonal = (project in file("hexagonal"))
  .settings(
    commonSettings,
    name                 := "hexagonal",
    libraryDependencies ++= Seq(
      // logback,
      quillJdbc,
      quillPostgres,
      postgresDB,
      slick,
      slickPool,
      typesafeConfig,
      zio,
      zioPrelude,
      h2Database,
      scalaTest
    ),
    parallelExecution    := false,
    Compile / run / fork := true,
    assemblyStrategy
  )
  .dependsOn(quillMacros, testUtils)

lazy val assemblyStrategy = ThisBuild / assemblyMergeStrategy := {
  case "module-info.class"                                    => MergeStrategy.last
  case "META-INF/versions/9/module-info.class"                => MergeStrategy.last
  case "META-INF/io.netty.versions.properties"                => MergeStrategy.last
  case "META-INF/maven/org.webjars/swagger-ui/pom.properties" => MergeStrategy.first
  case x                                                      =>
    val oldStrategy = assemblyMergeStrategy.value
    oldStrategy(x)
}

lazy val specification = (project in file("specification"))
  .settings(
    commonSettings,
    name := "specification"
  )

lazy val zioConfig = (project in file("zio-config"))
  .settings(
    commonSettings,
    name := "zio config poc",
    libraryDependencies ++= Seq(
      zio,
      zioConfigLib,
      zioConfigTypesafe
    )
  )

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

enablePlugins(PrintModulesTask)
