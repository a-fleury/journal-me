package com.journalme
package domain.error

sealed trait SessionError
case class ConfigLoadError(err: com.journalme.config.ConfigError) extends SessionError
case object InvalidUser extends SessionError
case class InvalidToken(token: String) extends SessionError
