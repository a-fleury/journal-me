package com.journalme
package domain.service

import cats.effect.IO
import domain.error.{UserError, UserNotFound}
import domain.model.User
import persistence.repository.UserRepository

import java.util.UUID

class UserService(
  userRepo: UserRepository
) {

  def create(user: User): IO[Either[UserError, User]] =
    userRepo.save(user).map(Right(_))

  def getById(id: UUID): IO[Either[UserError, User]] =
    userRepo.findById(id).map {
      case Some(user) => Right(user)
      case None       => Left(UserNotFound)
    }

  def getByEmail(email: String): IO[Either[UserError, User]] =
    userRepo.findByEmail(email).map {
      case Some(user) => Right(user)
      case None       => Left(UserNotFound)
    }
}
