package com.journalme
package persistence.doobie

import config.Env

import cats.effect.*
import doobie.LogHandler
import doobie.hikari.*

import scala.concurrent.ExecutionContext

object Database:

  private val logHandler: Option[LogHandler[IO]] =
    Env.getString("ENVIRONMENT") match
      case Right("dev") => Some(LogHandler.jdkLogHandler)
      case _            => None
      
  private val postgresUser: String = Env.getString("POSTGRES_USER").getOrElse("user")
  
  private val postgresPassword: String = Env.getString("POSTGRES_PASSWORD").getOrElse("pwd")

  private val postgresPort: String = Env.getString("POSTGRES_PORT").getOrElse("5432")

  def transactor: Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      driverClassName = "org.postgresql.Driver",
      url = s"jdbc:postgresql://localhost:${postgresPort}/journalme",
      user = postgresUser,
      pass = postgresPassword,
      connectEC = ExecutionContext.global,
      logHandler = logHandler
    )
