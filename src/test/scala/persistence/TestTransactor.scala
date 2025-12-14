package com.journalme
package persistence

import cats.effect.{IO, Resource}
import org.testcontainers.containers.PostgreSQLContainer

import scala.concurrent.ExecutionContext

import _root_.doobie.Transactor
import _root_.doobie.hikari.HikariTransactor

object TestTransactor {

  def resource(
    container: PostgreSQLContainer[_]
  ): Resource[IO, Transactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      driverClassName = "org.postgresql.Driver",
      url = container.getJdbcUrl,
      user = container.getUsername,
      pass = container.getPassword,
      connectEC = ExecutionContext.global,
      logHandler = None
    )
}
