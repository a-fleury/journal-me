package com.journalme
package persistence.repository

import cats.effect.IO
import domain.model.{Credentials, User}

import java.util.UUID

trait UserRepository {

  def save(user: User): IO[User]

  def findById(id: UUID): IO[Option[User]]

  def findByEmail(email: String): IO[Option[User]]
}
