package com.journalme
package domain.model

import io.circe.Codec
import sttp.tapir.Schema

enum Genre derives Codec, Schema {
    case Male, Female, Other
}