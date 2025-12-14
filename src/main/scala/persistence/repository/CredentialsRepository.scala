package com.journalme
package persistence.repository

import cats.effect.IO
import domain.model.Credentials

import java.util.UUID

trait CredentialsRepository {

  def save(credentials: Credentials): IO[Credentials]

  def findByUserId(userId: UUID): IO[Option[Credentials]]
}
