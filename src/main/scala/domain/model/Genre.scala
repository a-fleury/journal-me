package com.journalme
package domain.model

import io.circe.{Decoder, Encoder}
import sttp.tapir.{Schema, Validator}

enum Genre:
  case Male, Female, Other

object Genre:

  // -------- Circe (JSON) --------

  given Encoder[Genre] =
    Encoder.encodeString.contramap(_.toString)

  given Decoder[Genre] =
    Decoder.decodeString.emap {
      case "Male"   => Right(Genre.Male)
      case "Female" => Right(Genre.Female)
      case "Other"  => Right(Genre.Other)
      case other    => Left(s"Invalid genre: $other")
    }

  // -------- Tapir (schema / validation) --------

  given Schema[Genre] =
    Schema.string.validate(
      Validator.enumeration(
        List(Genre.Male, Genre.Female, Genre.Other),
        g => Some(g.toString) // 👈 THIS fixes the error
      )
    )
