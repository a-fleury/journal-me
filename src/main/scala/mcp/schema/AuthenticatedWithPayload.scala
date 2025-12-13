package com.journalme
package mcp.schema

import io.circe.Codec
import sttp.tapir.Schema

case class AuthenticatedWithPayload[T](
  token: String,
  payload: T
) derives Codec, Schema
