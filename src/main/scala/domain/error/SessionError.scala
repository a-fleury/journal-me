package com.journalme
package domain.error

import config.ConfigError

sealed trait SessionError
case class ConfigLoadError(err: ConfigError) extends SessionError
case object InvalidUser extends SessionError
case class InvalidToken(token: String) extends SessionError
