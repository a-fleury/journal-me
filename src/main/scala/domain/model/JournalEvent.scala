package com.journalme
package domain.model

import java.util.{Date, UUID}

final case class JournalEvent(
  id: UUID,
  title: String,
  description: Option[String],
  date: Date,
  startedAt: Option[Date],
  endedAt: Option[Date],
  user: User
)
