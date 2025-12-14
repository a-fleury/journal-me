package com.journalme
package persistence.doobie.model

import java.util.UUID
import java.time.Instant

case class DbSession(
  id: UUID,
  token: String,
  starts_at: Instant,
  ends_at: Instant,
  user_id: UUID
)
