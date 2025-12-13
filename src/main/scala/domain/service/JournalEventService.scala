package com.journalme
package domain.service

import domain.error.*
import domain.model.{JournalEvent, User}
import domain.repository.{JournalEventRepository, UserRepository}
import mcp.schema.AddJournalEvent

import java.time.Instant
import java.util.UUID

class JournalEventService(
  journalEventRepo : JournalEventRepository,
  userRepo : UserRepository
) {
  def getAllLastMonth(userId: UUID): Either[JournalEventError, List[JournalEvent]] = {
    val events = journalEventRepo.findAllLastMonth(userId)

    if (events.isEmpty) Left(NoJournalEventsFound)
    else Right(events)
  }

  def getAllWithinDates(userId: UUID, startDate: Instant, endDate: Instant): Either[JournalEventError, List[JournalEvent]] = {
    val now = Instant.now()

    if (startDate.isAfter(now)) return Left(InvalidStartDate)
    else if (endDate.isBefore(startDate)) return Left(InvalidDateInterval)
    else if (endDate.isAfter(now)) return Left(InvalidEndDate)

    val events: List[JournalEvent] = journalEventRepo.findAllLastMonth(userId)

    if (events.isEmpty) Left(NoJournalEventsFound)
    else Right(journalEventRepo.findAllWithinDates(userId, startDate, endDate))
  }

  def add(request : AddJournalEvent, userId : UUID): Either[JournalEventError, JournalEvent] = {
    val now = Instant.now()

    if (request.startedAt.exists(_.isAfter(now))) Left(InvalidStartDate)
    else if (request.endedAt.exists(_.isAfter(now))) Left(InvalidEndDate)
    else if (request.startedAt.exists(_.isAfter(request.endedAt.get))) Left(InvalidDateInterval)
    else {
      for
        user <- userRepo.findById(userId).toRight(InvalidUserId) // TODO: replace with user service
        event = buildJournalEvent(request, user)
        saved = journalEventRepo.save(event)
      yield saved
    }
  }

  private def buildJournalEvent(request: AddJournalEvent, user: User): JournalEvent =
    val now = Instant.now()
    val startedAt = request.startedAt.getOrElse(now)
    val endedAt = request.endedAt.getOrElse(now)
    val eventId = UUID.randomUUID
    JournalEvent(
      eventId,
      request.title,
      request.description,
      now,
      startedAt,
      endedAt,
      user
    )
}
