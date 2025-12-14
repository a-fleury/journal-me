package com.journalme
package domain.error

sealed trait UserError {
  def message: String
}

case object UserAlreadyExists extends UserError {
  val message: String =
    "A user with this email already exists."
}

case object UserNotFound extends UserError {
  val message: String =
    "User not found."
}

case object InvalidCredentials extends UserError {
  val message: String =
    "Invalid email or password."
}

case object InvalidEmail extends UserError {
  val message: String =
    "The provided email address is not valid."
}

final case class SessionCreationError(err: SessionError) extends UserError {
  def message: String =
    "Error during the creation of a session for the user."
}

final case class LogoutError(err: SessionError) extends UserError {
  def message: String =
    "An error occurred while logging out. Your token was probably already expired !"
}
