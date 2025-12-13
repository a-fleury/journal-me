package com.journalme
package domain.error

sealed trait UserError
case object UserAlreadyExists extends UserError
case object UserNotFound extends UserError
case object InvalidCredentials extends UserError
case object InvalidEmail extends UserError
class SessionCreationError(err: SessionError) extends UserError
class LogoutError(err: SessionError) extends UserError