package com.journalme
package domain.error

sealed trait JournalEventError {
  def message: String
}

case object InvalidStartDate extends JournalEventError {
  val message = "Start date cannot be in the future."
}

case object InvalidEndDate extends JournalEventError {
  val message = "End date cannot be in the future."
}

case object InvalidDateInterval extends JournalEventError {
  val message = "End date must be after start date."
}

case object NoJournalEventsFound extends JournalEventError {
  val message = "No journal events were found for the given criteria."
}

case object InvalidUserId extends JournalEventError {
  val message = "The provided user ID does not exist."
}
