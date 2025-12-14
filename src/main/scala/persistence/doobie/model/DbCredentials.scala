package com.journalme
package persistence.doobie.model

import java.util.UUID

case class DbCredentials(
  user_id: UUID,
  password_hash: String
)
