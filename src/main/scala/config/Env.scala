package com.journalme
package config

object Env {

  def getPositiveInt(name: String): Either[ConfigError, Int] =
    sys.env.get(name) match
      case None =>
        Left(MissingEnv(name))

      case Some(value) =>
        value.toIntOption match
          case None =>
            Left(InvalidInt(name, value))
          case Some(intValue) if intValue <= 0 =>
            Left(NonPositiveInt(name, intValue))
          case Some(intValue) =>
            Right(intValue)

  def getString(name: String): Either[ConfigError, String] =
    sys.env.get(name) match
      case None =>
        Left(MissingEnv(name))
      case Some(value) =>
        value match
          case "" =>
            Left(EmptyString(name, value)) 
          case _ => Right(value)
}
