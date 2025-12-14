package com.journalme
package domain.error

import config.ConfigError

sealed trait SessionError {
  def message: String
}

case class ConfigLoadError(err: ConfigError) extends SessionError {
  def message: String =
    s"Failed to load session configuration. ${err.toString}"
}

case object InvalidUser extends SessionError {
  val message: String =
    "The user associated with this session does not exist."
}

case class InvalidToken(token: String) extends SessionError {
  def message: String =
    "The provided session token is invalid or has expired."
}
