package com.journalme

import cats.effect.std.Dispatcher
import cats.effect.{IO, IOApp}
import chimp.*
import com.journalme.persistence.repository.{CredentialsRepository, JournalEventRepository, SessionRepository, UserRepository}
import domain.service.*
import mcp.tool.{AuthTools, JournalEventTools}
import persistence.doobie.repository.{DoobieCredentialsRepository, DoobieJournalEventRepository, DoobieSessionRepository, DoobieUserRepository}
import sttp.tapir.server.netty.cats.NettyCatsServer

object Main extends IOApp.Simple {

  override def run: IO[Unit] = {

    // ------------------------------------------------------------------
    // 1. Allocate resources
    // ------------------------------------------------------------------

    val resources =
      for {
        userRepo    <- DoobieUserRepository.resource
        sessionRepo <- DoobieSessionRepository.resource
        journalEventRepo <- DoobieJournalEventRepository.resource
        credentialsRepo <- DoobieCredentialsRepository.resource
        dispatcher       <- Dispatcher.parallel[IO]
      } yield (userRepo, sessionRepo, journalEventRepo, credentialsRepo, dispatcher)

    // ------------------------------------------------------------------
    // 2. Use resources to start MCP server
    // ------------------------------------------------------------------

    resources.use { case (userRepo : UserRepository, 
    sessionRepo: SessionRepository, 
    journalEventRepo: JournalEventRepository,
    credentialsRepo: CredentialsRepository, 
    dispatcher: Dispatcher[IO]) =>
      

      // Services
      val userService = new UserService(userRepo = userRepo)
      
      val sessionService = new SessionService(sessionRepo = sessionRepo)
      
      val journalEventService = new JournalEventService(journalEventRepo = journalEventRepo, userService = userService)
      
      val authService = new AuthService(credentialsRepo = credentialsRepo, userService = userService, sessionService = sessionService)

      // Tools
      val authTools = new AuthTools(authService = authService)
      
      val journalEventTools = new JournalEventTools(journalEventService = journalEventService, sessionService = sessionService)

      val tools =
        List(
          authTools.register,
          authTools.login,
          authTools.logout,
          journalEventTools.addJournalEvent,
          journalEventTools.getJournalEventsWithinPeriod,
          journalEventTools.getLastSimilarJournalEvent
        )

      // MCP endpoint
      val endpoint =
        mcpEndpoint(tools, List("mcp"))

      NettyCatsServer(dispatcher)
        .port(8080)
        .addEndpoint(endpoint)
        .start()
        .flatMap(_ => IO.never)
    }
  }
}
