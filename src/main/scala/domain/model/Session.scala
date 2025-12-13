package com.journalme
package domain.model

import io.circe.Codec
import sttp.tapir.Schema

import java.util.UUID
import java.time.Instant

final case class Session(
  id: UUID,
  token: String,
  startsAt: Instant,
  endsAt: Instant,
  user: User
) derives Codec, Schema
