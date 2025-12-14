package com.journalme
package persistence.doobie.repository

import domain.model.Session
import persistence.doobie.Database
import persistence.doobie.mapper.{JournalEventMapper, SessionMapper}
import persistence.doobie.model.*
import persistence.repository.SessionRepository

import cats.effect.{IO, Resource}
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*

final class DoobieSessionRepository private (
  xa: Transactor[IO]
) extends SessionRepository {

  private val selectWithUser =
    fr"""
        SELECT 
        s.id, s.token, s.starts_at, s.ends_at, s.user_id
        FROM sessions s 
        JOIN users u ON u.id = s.user_id
        """

  override def save(session: Session): IO[Session] = {
    val dbSession = SessionMapper.toDb(session)
    
    sql"""
         INSERT INTO sessions
         (id, token, starts_at, ends_at, user_id) 
         VALUES 
         (${dbSession.id}, 
         ${dbSession.token}, 
         ${dbSession.starts_at}, 
         ${dbSession.ends_at}, 
         ${dbSession.user_id})
       """
      .update
      .run
      .transact(xa)
      .as(session)
  }

  override def findActiveByToken(token: String): IO[Option[Session]] = {
    (selectWithUser ++
      fr"""
        WHERE s.token = $token
        AND s.ends_at > now()
        """
      )
      .query[(DbSession, DbUser)]
      .option
      .map(_.map(SessionMapper.toDomain))
      .transact(xa)
  }
}

object DoobieSessionRepository {
  def resource: Resource[IO, DoobieSessionRepository] =
    Database.transactor.map(new DoobieSessionRepository(_))
}
