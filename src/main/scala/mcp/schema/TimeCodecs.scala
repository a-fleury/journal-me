package mcp.schema

import io.circe.Decoder
import java.time.{Instant, OffsetDateTime}

/**
 * Tolerant Instant decoder for LLM / MCP inputs.
 *
 * Accepts:
 * - 2025-12-14T12:00:00Z
 * - 2025-12-14T12:00:00+01:00
 */
given Decoder[Instant] =
Decoder.decodeString.emap { s =>
  // First try strict Instant (UTC with Z)
  try Right(Instant.parse(s))
  catch {
    case _: Exception =>
      // Then try OffsetDateTime and normalize
      try Right(OffsetDateTime.parse(s).toInstant)
      catch {
        case _: Exception =>
          Left(
            "Invalid date-time. Expected ISO-8601, e.g. " +
              "2025-12-14T12:00:00Z or 2025-12-14T12:00:00+01:00"
          )
      }
  }
}
