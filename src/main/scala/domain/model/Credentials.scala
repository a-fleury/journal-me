package com.journalme
package domain.model

import java.util.UUID

final case class Credentials(
  userId: UUID,
  passwordHash: String,
)
