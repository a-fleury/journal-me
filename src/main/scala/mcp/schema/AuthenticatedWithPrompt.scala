package com.journalme
package mcp.schema

import io.circe.Codec
import sttp.tapir.Schema

case class AuthenticatedWithPrompt(
  token: String,
  userPrompt: String
) derives Codec, Schema
