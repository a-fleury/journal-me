package com.journalme
package mcp.schema

import io.circe.Codec
import sttp.tapir.Schema

import java.time.Instant

case class GetRapport(
  from: Instant,
  until: Instant
) derives Codec, Schema
