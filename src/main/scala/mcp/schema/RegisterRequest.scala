package com.journalme
package mcp.schema

import domain.model.Genre
import io.circe.Codec
import sttp.tapir.Schema

case class RegisterRequest(
  firstName: String,
  lastName: String,
  email: String,
  password: String,
  genre: Genre
) derives Codec, Schema
