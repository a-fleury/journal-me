package com.journalme
package mcp.schema

import io.circe.Codec
import sttp.tapir.Schema

case class LoginRequest(
  email: String,
  password: String
) derives Codec, Schema
