package com.journalme
package persistence.repository

import persistence.doobie.repository.DoobieUserRepository
import domain.model.{Genre, User}
import persistence.{PostgresTestContainer, TestTransactor}

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import doobie.{Fragment, Transactor}
import _root_.doobie.implicits.*
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID
import scala.io.Source

def initSchema(xa: Transactor[IO]): IO[Unit] = {
  val schema =
    Source
      .fromResource("schema.sql")
      .mkString

  Fragment
    .const(schema)
    .update
    .run
    .transact(xa)
    .void
}


class DoobieUserRepositorySpec
  extends AsyncFreeSpec
    with AsyncIOSpec
    with Matchers {

  "DoobieUserRepository" - {

    "findByEmail returns user when present" in {
      val container = new PostgresTestContainer()
      container.start()

      val test =
        TestTransactor.resource(container).use { xa =>
          for {
            _ <- initSchema(xa)
            repo = new DoobieUserRepository(xa)

            user = User(
              id = UUID.randomUUID(),
              firstName = "Alex",
              lastName = "F",
              email = "alex@test.com",
              genre = Genre.Male
            )

            _ <- repo.save(user)
            found <- repo.findByEmail(user.email)
          } yield {
            found shouldBe Some(user)
          }
        }

      test.guarantee(IO(container.stop())).unsafeToFuture()
    }
  }
}

