package com.journalme
package domain.service

import config.Env
import domain.error.{ConfigLoadError, InvalidToken, InvalidUser, SessionError}
import domain.model.{Session, User}
import persistence.repository.SessionRepository
import cats.effect.IO

import java.time.Instant
import java.util.UUID

class SessionService(
  sessionRepo : SessionRepository
) {

  private lazy val sessionLength = Env.getPositiveInt("SESSION_LENGTH")

  def create(user: User): IO[Either[SessionError, Session]] =
    for
      lengthEither <- IO.pure(sessionLength.left.map(ConfigLoadError.apply))
      result <- lengthEither match
        case Left(err) => IO.pure(Left(err))
        case Right(length) =>
          validateUser(user) match
            case Left(err) => IO.pure(Left(err))
            case Right(validUser) =>
              val session = buildSession(validUser, length)
              sessionRepo.save(session).map(Right(_))
    yield result

  def refresh(token: String): IO[Either[SessionError, Session]] =
    IO.pure(sessionLength.left.map(ConfigLoadError.apply)).flatMap {
      case Left(err) => IO.pure(Left(err))

      case Right(length) =>
        findActiveSessionByToken(token).flatMap {
          case Left(err) => IO.pure(Left(err))

          case Right(session) =>
            val now = Instant.now()
            val refreshed =
              session.copy(
                endsAt = Instant.ofEpochMilli(now.toEpochMilli + length * 1000)
              )

            sessionRepo.refresh(refreshed).map(Right(_))
        }
    }

  def end(token: String): IO[Either[SessionError, Session]] =
    findActiveSessionByToken(token).flatMap {
      case Left(err) => IO.pure(Left(err))

      case Right(session) =>
        val now = Instant.now()
        val ended = session.copy(endsAt = now)

        sessionRepo.end(ended).map(Right(_))
    }

  private def generateToken(): String =
    java.util.UUID.randomUUID().toString.replace("-", "")

  private def validateUser(user: User): Either[SessionError, User] =
    if user == null || user.id == null then Left(InvalidUser) else Right(user)

  // Call this method after validating user and length
  private def buildSession(user: User, length: Int): Session =
    val now = Instant.now()
    val endsAt = Instant.ofEpochMilli(now.toEpochMilli + length * 1000)
    val sessionId = UUID.randomUUID
    val token = generateToken()
    Session(sessionId, token, now, endsAt, user)

  private def findActiveSessionByToken(token: String): IO[Either[SessionError, Session]] =
    sessionRepo.findActiveByToken(token).map {
      case Some(session) => Right(session)
      case None          => Left(InvalidToken(token))
    }
}
