package com.journalme
package domain.model

import java.util.{Date, UUID}

final case class Session(
  id: UUID,
  token: String,
  startsAt: Date,
  endsAt: Date,
  user: User
)
