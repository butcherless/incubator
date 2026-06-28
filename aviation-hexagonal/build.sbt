import Dependencies.*

ThisBuild / organization := "com.aviation"
ThisBuild / scalaVersion := Versions.scala3  // 3.3.8 LTS
ThisBuild / version      := "0.1.0-SNAPSHOT"

ThisBuild / scalacOptions := Seq(
  "-encoding", "utf8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Wunused:all"
)

ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

// ─── Common settings ────────────────────────────────────────────────────────

lazy val commonSettings = Seq(
  libraryDependencies ++= commonTest
)

// ─── Modules ────────────────────────────────────────────────────────────────

lazy val root = project
  .in(file("."))
  .aggregate(
    sharedKernel,
    domain,
    application,
    persistencePostgres,
    messagingKafka,
    migration,
    adapterHttp,
    bootstrap
  )
  .settings(
    name          := "aviation-hexagonal",
    publish / skip := true
  )

lazy val sharedKernel = project
  .in(file("shared-kernel"))
  .settings(commonSettings)
  .settings(
    name := "shared-kernel",
    libraryDependencies ++= Seq(zio)
  )

lazy val domain = project
  .in(file("domain"))
  .dependsOn(sharedKernel)
  .settings(commonSettings)
  .settings(
    name := "domain",
    libraryDependencies ++= Seq(zio)
  )

lazy val application = project
  .in(file("application"))
  .dependsOn(domain)
  .settings(commonSettings)
  .settings(
    name := "application",
    libraryDependencies ++= Seq(zio)
  )

lazy val persistencePostgres = project
  .in(file("infrastructure/persistence-postgres"))
  .dependsOn(domain)
  .settings(commonSettings)
  .settings(
    name := "persistence-postgres",
    libraryDependencies ++= Seq(
      doobieCore,
      doobieHikari,
      doobiePostgres,
      zioInteropCats,
      postgresql,
      hikaricp
    )
  )

lazy val messagingKafka = project
  .in(file("infrastructure/messaging-kafka"))
  .dependsOn(domain)
  .settings(commonSettings)
  .settings(
    name := "messaging-kafka",
    libraryDependencies ++= Seq(
      zioKafka,
      circeCore,
      circeGeneric,
      circeParser
    )
  )

lazy val migration = project
  .in(file("infrastructure/migration"))
  .settings(commonSettings)
  .settings(
    name := "migration",
    libraryDependencies ++= Seq(
      flywayCore,
      flywayPostgres,
      postgresql,
      zio
    )
  )

lazy val adapterHttp = project
  .in(file("adapter-http"))
  .dependsOn(domain, application)
  .settings(commonSettings)
  .settings(
    name := "adapter-http",
    libraryDependencies ++= Seq(
      tapirCore,
      tapirZioHttp,
      tapirJsonCirce,
      tapirSwaggerUi,
      zioHttp,
      circeCore,
      circeGeneric,
      circeParser
    )
  )

lazy val bootstrap = project
  .in(file("bootstrap"))
  .dependsOn(domain, application, persistencePostgres, messagingKafka, migration, adapterHttp)
  .enablePlugins(JavaAppPackaging)
  .settings(commonSettings)
  .settings(
    name := "bootstrap",
    libraryDependencies ++= Seq(
      zio,
      zioLogging,
      zioLoggingSlf4j,
      logback
    ),
    assembly / assemblyMergeStrategy := {
      case PathList("module-info.class")                          => MergeStrategy.discard
      case PathList("META-INF", "versions", _, "module-info.class") => MergeStrategy.discard
      case PathList("META-INF", xs @ _*)                         => MergeStrategy.discard
      case "deriving.conf"                                        => MergeStrategy.concat
      case "reference.conf"                                       => MergeStrategy.concat
      case x =>
        val old = (assembly / assemblyMergeStrategy).value
        old(x)
    },
    assembly / mainClass := Some("bootstrap.OpenApiGenerator")
  )
