package com.journalme
package persistence.repository

import cats.effect.IO
import domain.model.Session

trait SessionRepository {

  def save(session: Session): IO[Session]

  def findActiveByToken(token: String): IO[Option[Session]]
}
