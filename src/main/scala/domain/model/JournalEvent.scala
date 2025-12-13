package com.journalme
package domain.model

import io.circe.Codec
import sttp.tapir.Schema

import java.util.UUID
import java.time.Instant

final case class JournalEvent(
  id: UUID,
  title: String,
  description: String,
  date: Instant,
  startedAt: Instant,
  endedAt: Instant,
  user: User
) derives Codec, Schema
