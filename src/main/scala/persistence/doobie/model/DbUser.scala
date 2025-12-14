package com.journalme
package persistence.doobie.model

import domain.model.Genre

import java.util.UUID
import java.time.Instant

case class DbUser(
  id: UUID,
  first_name: String,
  last_name: String,
  email: String,
  genre: String
)
