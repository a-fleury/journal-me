package com.journalme
package domain.service

import domain.error.*
import domain.model.{JournalEvent, User}
import mcp.schema.AddJournalEvent
import persistence.repository.JournalEventRepository

import java.time.Instant
import java.util.UUID
import cats.effect.IO

class JournalEventService(
  journalEventRepo : JournalEventRepository,
  userService: UserService
) {
  def getAllLastMonth(userId: UUID): IO[Either[JournalEventError, List[JournalEvent]]] = {
    journalEventRepo
      .findAllLastMonth(userId)
      .map {
        case Nil => Left(NoJournalEventsFound)
        case events => Right(events)
      }
  }

  def getAllWithinDates(userId: UUID, startDate: Instant, endDate: Instant): IO[Either[JournalEventError, List[JournalEvent]]] = {

    val now = Instant.now()

    validateDates(startDate, endDate, now) match
      case Left(err) =>
        IO.pure(Left(err))

      case Right(_) =>
        journalEventRepo
          .findAllWithinDates(userId, startDate, endDate)
          .map {
            case Nil    => Left(NoJournalEventsFound)
            case events => Right(events)
          }
  }

  def add(request : AddJournalEvent, userId : UUID): IO[Either[JournalEventError, JournalEvent]] = {

    val now = Instant.now()

    validateRequest(request, now) match
      case Left(err) =>
        IO.pure(Left(err))

      case Right(_) =>
        userService.getById(userId).flatMap {
          case Left(_) =>
            IO.pure(Left(InvalidUserId)) // possible upgrade : map UserError → JournalEventError

          case Right(user) =>
            val event = buildJournalEvent(request, user)
            journalEventRepo
              .save(event)
              .map(Right(_))
        }
          
  }

  private def buildJournalEvent(request: AddJournalEvent, user: User): JournalEvent =
    val eventId = UUID.randomUUID
    JournalEvent(
      eventId,
      request.title,
      request.description,
      request.date,
      request.startedAt,
      request.endedAt,
      user
    )

  private def validateDates(startDate: Instant, endDate: Instant, now: Instant): Either[JournalEventError, Unit] =
    if (startDate.isAfter(now)) Left(InvalidStartDate)
    else if (endDate.isBefore(startDate)) Left(InvalidDateInterval)
    else if (endDate.isAfter(now)) Left(InvalidEndDate)
    else Right(())

  private def validateRequest(
    request: AddJournalEvent,
    now: Instant
  ): Either[JournalEventError, Unit] =
    if (request.startedAt.isAfter(now))
      Left(InvalidStartDate)
    else if (request.endedAt.isAfter(now))
      Left(InvalidEndDate)
    else if (
        request.startedAt.isAfter(request.endedAt)
    )
      Left(InvalidDateInterval)
    else
      Right(())

}
