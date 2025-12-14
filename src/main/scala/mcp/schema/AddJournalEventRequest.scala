package com.journalme
package mcp.schema

import io.circe.Codec
import sttp.tapir.Schema

import java.time.Instant

import com.journalme.mcp.schema.given


case class AddJournalEventRequest(
  token: String,
  title: String,
  description: String,
  date: Instant,
  startedAt: Instant,
  endedAt: Instant
) derives Codec, Schema

