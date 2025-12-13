package com.journalme
package mcp.schema

import io.circe.Codec
import sttp.tapir.Schema

case class Authenticated(
  token: String
) derives Codec, Schema
