package com.journalme
package domain.repository

import domain.model.Session

import java.util.UUID

trait SessionRepository {
  def save(session: Session): Session
  def findActiveByToken(token: String): Option[Session]
}
