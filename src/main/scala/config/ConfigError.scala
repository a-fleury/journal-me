package com.journalme
package config

sealed trait ConfigError
case class MissingEnv(name: String) extends ConfigError
case class InvalidInt(name: String, value: String) extends ConfigError
case class NonPositiveInt(name: String, value: Int) extends ConfigError
case class EmptyString(name: String, value: String) extends ConfigError

