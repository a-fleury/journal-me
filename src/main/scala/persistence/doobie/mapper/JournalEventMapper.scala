package com.journalme
package persistence.doobie.mapper

import domain.model.{JournalEvent, User}
import persistence.doobie.model.{DbJournalEvent, DbUser}

object JournalEventMapper:

  def toDomain(dbEvent: DbJournalEvent, dbUser: DbUser): JournalEvent =
    val user = UserMapper.toDomain(dbUser)
    JournalEvent(
      id = dbEvent.id,
      title = dbEvent.title,
      description = dbEvent.description,
      date = dbEvent.date,
      startedAt = dbEvent.started_at,
      endedAt = dbEvent.ended_at,
      user = user
    )
    
  def toDb(domain: JournalEvent): DbJournalEvent =
    DbJournalEvent(
      id = domain.id,
      title = domain.title,
      description = domain.description,
      date = domain.date,
      started_at = domain.startedAt,
      ended_at = domain.endedAt,
      user_id = domain.user.id
    )
