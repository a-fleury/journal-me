package com.journalme
package persistence.repository

import domain.model.JournalEvent
import cats.effect.IO

import java.util.UUID
import java.time.Instant

trait JournalEventRepository {
  def save(event: JournalEvent): IO[JournalEvent]
  def findAllLastMonth(userId: UUID): IO[List[JournalEvent]]
  def findAllWithinDates(
    userId: UUID,
    startDate: Instant,
    endDate: Instant
  ): IO[List[JournalEvent]]
}

