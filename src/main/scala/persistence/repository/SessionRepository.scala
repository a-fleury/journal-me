package com.journalme
package persistence.repository

import cats.effect.IO
import domain.model.Session

import java.time.Instant

trait SessionRepository {

  def save(session: Session): IO[Session]

  def refresh(session: Session): IO[Session]
  
  def end(session: Session): IO[Session]
  
  def findActiveByToken(token: String): IO[Option[Session]]
}
