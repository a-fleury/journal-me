package com.journalme
package domain.service

import domain.error.*
import domain.model.{Credentials, PasswordHasher, Session, User}
import mcp.schema.{Authenticated, LoginRequest, LoginResponse, RegisterRequest}
import persistence.repository.CredentialsRepository

import java.util.UUID
import cats.effect.IO

class AuthService(
  credentialsRepo: CredentialsRepository,
  userService: UserService,
  sessionService: SessionService
) {

  def login(request: LoginRequest): IO[Either[UserError, LoginResponse]] =
    userService.getByEmail(request.email).flatMap {
      case Left(err) => IO.pure(Left(err))

      case Right(user) =>
        credentialsRepo.findByUserId(user.id).flatMap {
          case None => IO.pure(Left(InvalidCredentials))

          case Some(credentials) =>
            verifyPassword(request.password, credentials.passwordHash) match {
              case Left(err) => IO.pure(Left(err))

              case Right(_) =>
                sessionService.create(user).map {
                  case Left(err) => Left(SessionCreationError(err))
                  case Right(session) =>
                    Right(
                      LoginResponse(
                        user = user,
                        session = session
                      )
                    )
                }
            }
        }
    }


  def register(request: RegisterRequest): IO[Either[UserError, User]] =
    ensureEmailAvailable(request.email).flatMap {
      case Left(err) => IO.pure(Left(err))

      case Right(_) =>
        val user = User(
          id = UUID.randomUUID,
          firstName = request.firstName,
          lastName = request.lastName,
          email = request.email,
          genre = request.genre
        )

        val credentials = Credentials(
          userId = user.id,
          passwordHash = PasswordHasher.hash(request.password)
        )

        userService.create(user).flatMap {
          case Left(err) => IO.pure(Left(err))

          case Right(savedUser) =>
            createCredentials(credentials).map {
              case Left(err) => Left(err)
              case Right(_)  => Right(savedUser)
            }
        }
    }


  private def logout(authenticated: Authenticated): IO[Either[UserError, Session]] =
    sessionService.end(authenticated.token).map {
      case Left(err) => Left(LogoutError(err))
      case Right(s)  => Right(s)
    }


  private def verifyPassword(password: String, hash: String): Either[UserError, Unit] =
    Either.cond(
      PasswordHasher.verify(password, hash),
      (),
      InvalidCredentials
    )

  private def ensureEmailAvailable(email: String): IO[Either[UserError, Unit]] =
    userService.getByEmail(email).map {
      case Right(_) => Left(UserAlreadyExists)
      case Left(_)  => Right(())
    }


  private def createCredentials(credentials: Credentials): IO[Either[UserError, Unit]] =
    credentialsRepo.save(credentials).map(_ => Right(()))


}
