package com.journalme
package mcp.schema

import io.circe.Codec
import sttp.tapir.Schema

import java.time.Instant


case class GetEventsWithinPeriodRequest(
  token: String,
  from: Instant,
  until: Instant,
  userPrompt: String
) derives Codec, Schema