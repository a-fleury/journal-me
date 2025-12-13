package com.journalme
package mcp.schema

import domain.model.{Session, User}
import io.circe.Codec
import sttp.tapir.Schema

case class LoginResponse(
  user: User,
  session: Session
) derives Codec, Schema
