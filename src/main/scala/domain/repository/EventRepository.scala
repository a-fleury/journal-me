package com.journalme
package domain.repository

import domain.model.JournalEvent

import java.util.{Date, UUID}

trait EventRepository {
  def save(event: JournalEvent): JournalEvent
  def findAllLastMonth(userId : UUID): List[JournalEvent]
  def findAllWithinDates(userId : UUID, startDate: Date, endDate: Date): List[JournalEvent]
}
