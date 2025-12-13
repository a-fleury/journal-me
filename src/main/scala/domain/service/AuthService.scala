package com.journalme
package domain.service

import domain.error.{InvalidCredentials, LogoutError, SessionCreationError, UserAlreadyExists, UserError}
import domain.model.{Credentials, PasswordHasher, Session, User}
import domain.repository.CredentialsRepository
import mcp.schema.{Authenticated, LoginRequest, LoginResponse, RegisterRequest}

import java.util.UUID

class AuthService(
  credentialsRepo: CredentialsRepository,
  userService: UserService,
  sessionService: SessionService
) {

  def login(request: LoginRequest): Either[UserError, LoginResponse] = {
    for {
      user <- userService.getByEmail(request.email)
      credentials <- credentialsRepo.findByUserId(user.id).toRight(InvalidCredentials)
      _ <- verifyPassword(request.password, credentials.passwordHash)
      session <- sessionService.create(user: User).left.map(SessionCreationError.apply)
    }
      yield LoginResponse(
        user = user,
        session = session
      )
  }

  def register(request: RegisterRequest): Either[UserError, User] =
    for {
      _ <- ensureEmailAvailable(request.email)

      user = User(
        id = UUID.randomUUID,
        firstName = request.firstName,
        lastName = request.lastName,
        email = request.email,
        genre = request.genre
      )

      passwordHash = PasswordHasher.hash(request.password)

      credentials = Credentials(
        userId = user.id,
        passwordHash = passwordHash
      )

      savedUser <- userService.create(user)
      _ <- createCredentials(credentials)

    } yield savedUser

  private def logout(authenticated: Authenticated): Either[UserError, Session] =
    sessionService.end(authenticated.token).left.map(LogoutError.apply)

  private def verifyPassword(password: String, hash: String): Either[UserError, Unit] =
    Either.cond(
      PasswordHasher.verify(password, hash),
      (),
      InvalidCredentials
    )

  private def ensureEmailAvailable(email: String): Either[UserError, Unit] =
    userService.getByEmail(email) match
      case Right(_) => Left(UserAlreadyExists)
      case Left(_) => Right(())

  private def createCredentials(credentials: Credentials): Either[UserError, Unit] =
    Right(credentialsRepo.save(credentials))

}
