package com.journalme
package domain.repository

import domain.model.JournalEvent

import java.util.UUID
import java.time.Instant

trait JournalEventRepository {
  def save(event: JournalEvent): JournalEvent
  def findAllLastMonth(userId : UUID): List[JournalEvent]
  def findAllWithinDates(userId : UUID, startDate: Instant, endDate: Instant): List[JournalEvent]
}
