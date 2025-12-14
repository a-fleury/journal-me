package com.journalme
package mcp.tool

import domain.service.{JournalEventService, SessionService}
import mcp.adapter.EventFormatter
import mcp.schema.{AddJournalEvent, AddJournalEventRequest, AuthenticatedWithPrompt, GetEventsWithinPeriodRequest}
import cats.effect.IO
import chimp.{ServerTool, tool}

final class JournalEventTools (
  journalEventService: JournalEventService,
  sessionService: SessionService
) {

  private val basicInstructions = "If you don't have every single information needed for the input, ask the user to provide them. Always provide a valid JSON object, not a string, matching the exact schema. ALL DATES MUST MATCH java.util.Instant format. If userPrompt is part of the schema, it means you need to provide the question asked by the user. This endpoint needs authentication, the user must provide a token."

  val addJournalEvent: ServerTool[AddJournalEventRequest, IO] = tool("addJournalEvent")
    .description("""
                   |IMPORTANT:
                   |- The `startedAt` and `endedAt` fields MUST be the same format as `date`.
                   |
                   |Example:
                   |User prompt : today, i coded from 10 am to 5pm
                   |
                   |{
                   |  "token": "<token>",
                   |  "title" : "Coding",
                   |  "description": "I coded from 10 am to 5pm",
                   |  "date": "2025-12-14T12:00:00Z",
                   |  "startedAt": "2025-12-14T10:00:00Z",
                   |  "endedAt": "2025-12-14T17:00:00Z"
                   |}
    """.stripMargin + basicInstructions + " " + "Add a new journal event to a user. If successful, returns a message with the event indicating that the event was added successfully. If not successful, returns an error indicating what went wrong.")
    .input[AddJournalEventRequest]
    .serverLogic[IO](
      (request, headers) => handleAddJournalEvent(request)
    )

  val getJournalEventsWithinPeriod: ServerTool[GetEventsWithinPeriodRequest, IO] = tool("getEventsWithinPeriod")
    .description("""
                   |IMPORTANT:
                   |- The `from` and `until` fields MUST have the same format
                   |
                   |Example:
                   |User prompt : What did I do this week ?
                   |
                   |{
                   |  "token": "<token>",
                   |  "from": "2025-12-07T12:00:00Z",
                   |  "until": "2025-12-14T12:00:00Z",
                   |  "userPrompt": "What did I do this week ?"
                   |}
    """.stripMargin + basicInstructions + " " + "Retrieve all the events a user has created within a specified time period. The time period must not exceed one month. If successful, returns a message with all the events that occured. If not successful, returns an error indicating what went wrong.")
    .input[GetEventsWithinPeriodRequest]
    .serverLogic[IO](
      (request, headers) => handleGetJournalEventsWithinPeriod(request)
    )

  val getLastSimilarJournalEvent: ServerTool[AuthenticatedWithPrompt, IO] = tool("getLastSimilarJournalEvent")
    .description(basicInstructions + " " + "Retrieve all the events a user has created within the last month. If successful, returns a message with all the events that occurred to let the LLM pick the one that was asked for. If not successful, returns an error indicating what went wrong.")
    .input[AuthenticatedWithPrompt]
    .serverLogic[IO](
      (request, headers) => handleGetLastSimilarJournalEvent(request)
    )

  private def handleAddJournalEvent(request: AddJournalEventRequest): IO[Either[String, String]] = {
    sessionService.refresh(request.token).flatMap {
      case Left(err) =>
        IO.pure(Left(s"Error refreshing the session ${err.message}"))

      case Right(session) =>
        journalEventService
          .add(AddJournalEvent(title = request.title,
            description = request.description,
            date = request.date,
            startedAt = request.startedAt,
            endedAt = request.endedAt), session.user.id)
          .map {
            case Left(err) =>
              Left(s"Error adding the event ${err.message}")

            case Right(event) =>
              Right(EventFormatter.addEventSuccess(event))
          }
    }
  }

  private def handleGetJournalEventsWithinPeriod(
    request: GetEventsWithinPeriodRequest
  ): IO[Either[String, String]] = {
    sessionService.refresh(request.token).flatMap {
      case Left(err) =>
        IO.pure(Left(s"Error refreshing the session ${err.message}"))

      case Right(session) =>
        journalEventService
          .getAllWithinDates(
            session.user.id,
            request.from,
            request.until
          )
          .map {
            case Left(err) =>
              Left(s"Error retrieving the events ${err.message}")

            case Right(events) =>
              Right(EventFormatter.getEventsWithinPeriodSuccess(events, request))
          }
    }
  }

  private def handleGetLastSimilarJournalEvent(request: AuthenticatedWithPrompt): IO[Either[String, String]] = {
    sessionService.refresh(request.token).flatMap {
      case Left(err) =>
        IO.pure(Left(s"Error refreshing the session ${err.message}"))

      case Right(session) =>
        journalEventService
          .getAllLastMonth(session.user.id)
          .map {
            case Left(err) =>
              Left(s"Error retrieving the events ${err.message}")

            case Right(events) =>
              Right(EventFormatter.getLastSimilarEventSuccess(events, request.userPrompt))
          }
    }
  }


}
