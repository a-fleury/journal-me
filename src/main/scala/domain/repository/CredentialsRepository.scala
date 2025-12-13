package com.journalme
package domain.repository

import java.util.UUID
import domain.model.Credentials

trait CredentialsRepository {
  def save(credentials: Credentials): Credentials
  def findByUserId(userId: UUID): Option[Credentials]
}
