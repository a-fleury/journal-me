package com.journalme
package mcp.schema

import io.circe.Codec
import java.time.Instant
import sttp.tapir.Schema

case class AddJournalEvent(
  title: String,
  description: String,
  date: Instant,
  startedAt: Instant,
  endedAt: Instant
) derives Codec, Schema
