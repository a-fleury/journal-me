package com.journalme
package persistence.doobie.repository

import cats.effect.{IO, Resource}
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*

import persistence.repository.JournalEventRepository
import persistence.doobie.Database
import persistence.doobie.mapper.JournalEventMapper
import persistence.doobie.model.*

import domain.model.JournalEvent

import java.time.Instant
import java.util.UUID

final class DoobieJournalEventRepository private (
  xa: Transactor[IO]
) extends JournalEventRepository {

  // ---------- SQL fragments ----------

  private val selectWithUser =
    fr"""
      SELECT
        e.id, e.title, e.description, e.date, e.started_at, e.ended_at, e.user_id,
        u.id, u.first_name, u.last_name, u.email, u.genre
      FROM journal_events e
      JOIN users u ON u.id = e.user_id
    """

  // ---------- Commands ----------

  override def save(event: JournalEvent): IO[JournalEvent] = {
    for {
      _ <- insert(event)
      saved <- findById(event.id)
    } yield saved
  }

  private def findById(id: UUID): IO[JournalEvent] =
    (selectWithUser ++ fr"WHERE e.id = $id")
      .query[(DbJournalEvent, DbUser)]
      .map(JournalEventMapper.toDomain)
      .unique
      .transact(xa)


  private def insert(event: JournalEvent): IO[Unit] = {
    val dbEvent = JournalEventMapper.toDb(event)

    sql"""
      INSERT INTO journal_events
        (id, title, description, date, started_at, ended_at, user_id)
      VALUES
        (${dbEvent.id}, ${dbEvent.title}, ${dbEvent.description},
         ${dbEvent.date}, ${dbEvent.started_at}, ${dbEvent.ended_at},
         ${dbEvent.user_id})
    """
      .update
      .run
      .transact(xa)
      .void // 👈 return the domain object after the effect
  }

  // ---------- Queries ----------

  override def findAllLastMonth(userId: UUID): IO[List[JournalEvent]] =
    (selectWithUser ++
      fr"""
        WHERE e.user_id = $userId
        AND e.date >= now() - interval '1 month'
        ORDER BY e.date DESC
      """
      )
      .query[(DbJournalEvent, DbUser)]
      .to[List]
      .map(_.map(JournalEventMapper.toDomain))
      .transact(xa)

  override def findAllWithinDates(
    userId: UUID,
    startDate: Instant,
    endDate: Instant
  ): IO[List[JournalEvent]] =
    (selectWithUser ++
      fr"""
        WHERE e.user_id = $userId
        AND e.date BETWEEN $startDate AND $endDate
        ORDER BY e.date DESC
      """
      )
      .query[(DbJournalEvent, DbUser)]
      .to[List]
      .map(_.map(JournalEventMapper.toDomain))
      .transact(xa)
}

object DoobieJournalEventRepository {
  def resource: Resource[IO, DoobieJournalEventRepository] =
    Database.transactor.map(new DoobieJournalEventRepository(_))
}
