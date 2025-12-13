package com.journalme
package domain.service

import domain.error.{ UserError, UserNotFound}
import domain.model.User
import domain.repository.UserRepository

import java.util.UUID
import scala.Right

class UserService(
  userRepo : UserRepository
) {
  def create(user: User): Either[UserError, User] =
    Right(userRepo.save(user))


  def getById(id: UUID): Either[UserError, User] = {
    userRepo.findById(id).toRight(UserNotFound)
  }
  
  def getByEmail(email: String): Either[UserError, User] = {
    userRepo.findByEmail(email).toRight(UserNotFound)
  }

}
