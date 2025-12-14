ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "JournalMe",

    libraryDependencies ++= Seq(
      "com.softwaremill.chimp" %% "core" % "0.1.6",
      "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.13.3",
      "com.softwaremill.sttp.tapir" %% "tapir-netty-server-cats" % "1.13.3",
      "ch.qos.logback" % "logback-classic" % "1.5.22",
      "org.mindrot" % "jbcrypt" % "0.4",
      "org.tpolecat" %% "doobie-core"      % "1.0.0-RC11",
      "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC11",
      "org.tpolecat" %% "doobie-hikari"    % "1.0.0-RC11",
      "org.typelevel" %% "cats-effect"     % "3.6.3",
      "org.typelevel" %% "cats-effect" % "3.6.3" % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.typelevel" %% "cats-effect-testing-scalatest" % "1.7.0" % Test,
      "org.testcontainers" % "postgresql" % "1.21.3" % Test
    )

  )
