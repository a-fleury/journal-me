package com.journalme
package persistence.doobie.model

import java.util.UUID
import java.time.Instant


case class DbJournalEvent(
  id: UUID,
  title: String,
  description: String,
  date: Instant,
  started_at: Instant,
  ended_at: Instant,
  user_id: UUID
)
