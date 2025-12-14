package com.journalme
package mcp.adapter

import domain.model.JournalEvent

import com.journalme.mcp.schema.GetEventsWithinPeriodRequest

object EventFormatter {

  def addEventSuccess(journalEvent: JournalEvent): String = {
    s"""Journal event added successfully.
       |Event:
       |  Title: ${journalEvent.title}
       |  Description: ${journalEvent.description}
       |  Date: ${journalEvent.date}
       |  Started at : ${journalEvent.startedAt}
       |  Ended at : ${journalEvent.endedAt}
       |
       |For the user :
       |  firstname: ${journalEvent.user.firstName}
       |  lastname: ${journalEvent.user.lastName}
       |  genre: ${journalEvent.user.genre}
       |
       |""".stripMargin
  }

  def getLastSimilarEventSuccess(journalEvents: List[JournalEvent], userPrompt: String): String = {
    s"""All events within last month for the user :
       |  firstname: ${journalEvents.head.user.firstName}
       |  lastname: ${journalEvents.head.user.lastName}
       |  genre: ${journalEvents.head.user.genre}
       |
       |User question:
       |  "${userPrompt}"
       |
       |Task:
       |From the events below, identify the most recent event that best matches the user question.
       |If there are multiple events that match the question, return the most recent one.
       |If there are no events that match the question, explain in your words that there are no similar events within the last month.
       |
       |${journalEvents
      .map { journalEvent =>
        s"""Event:
           |  Title: ${journalEvent.title}
           |  Description: ${journalEvent.description}
           |  Date: ${journalEvent.date}
           |  Started at: ${journalEvent.startedAt}
           |  Ended at: ${journalEvent.endedAt}
           |""".stripMargin
      }
      .mkString("\n")}
       |""".stripMargin
  }

  def getEventsWithinPeriodSuccess(journalEvents: List[JournalEvent], request: GetEventsWithinPeriodRequest ): String = {
    s"""All events within the time period specified by this user :
       |  firstname: ${journalEvents.head.user.firstName}
       |  lastname: ${journalEvents.head.user.lastName}
       |  genre: ${journalEvents.head.user.genre}
       |
       |The time period is from ${request.from} to ${request.until}.
       |
       |User question:
       |  "${request.userPrompt}"
       |
       |Task:
       |  From the events below, summarize what the user did during that time period.
       |  Do so in a way that is easy to understand and best answers the user's question.
       |  Start by the least recent event and work your way forward until the last one.
       |
       |${
      journalEvents
        .map { journalEvent =>
          s"""Event:
             |  Title: ${journalEvent.title}
             |  Description: ${journalEvent.description}
             |  Date: ${journalEvent.date}
             |  Started at: ${journalEvent.startedAt}
             |  Ended at: ${journalEvent.endedAt}
             |""".stripMargin
        }
        .mkString("\n")
    }
       |""".stripMargin
  }


}
