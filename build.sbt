ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "JournalMe",
    idePackagePrefix := Some("com.journalme"),

    libraryDependencies ++= Seq(
      "com.softwaremill.chimp" %% "core" % "0.1.6",
      "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.13.2",
      "com.softwaremill.sttp.tapir" %% "tapir-netty-server-sync" % "1.13.2",
      "ch.qos.logback" % "logback-classic" % "1.5.21",
      "io.github.cdimascio" % "dotenv-java" % "3.2.0"

)

  )
