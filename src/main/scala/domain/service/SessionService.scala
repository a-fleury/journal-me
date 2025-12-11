package com.journalme
package domain.service

import config.Env
import domain.error.{ConfigLoadError, InvalidToken, InvalidUser, SessionError}
import domain.model.{Session, User}
import domain.repository.SessionRepository

import java.util.{Date, UUID}

class SessionService(
  sessionRepo : SessionRepository
) {

  private lazy val sessionLength = Env.getPositiveInt("SESSION_LENGTH")

  def createSession(user: User): Either[SessionError, Session] =
    for
      length <- sessionLength.left.map(ConfigLoadError.apply)
      _ <- validateUser(user)
      session = buildSession(user, length)
      saved = sessionRepo.save(session)
    yield saved
    
  def refreshSession(token: String): Either[SessionError, Session] =
    for 
      length <- sessionLength.left.map(ConfigLoadError.apply)
      session <- findActiveSessionByToken(token)
      refreshed = 
        val now = new Date()
        session.copy(endsAt = new Date(now.getTime + length * 1000))
      saved = sessionRepo.save(refreshed)
    yield saved
    
  def endSession(token: String): Either[SessionError, Session] =
    for
      session <- findActiveSessionByToken(token)
      ended = 
        val now = new Date()
        session.copy(endsAt = now)
      saved = sessionRepo.save(ended)
    yield saved

  private def generateToken(): String =
    java.util.UUID.randomUUID().toString.replace("-", "")
    
  private def validateUser(user: User): Either[SessionError, User] =
    if user == null || user.id == null then Left(InvalidUser) else Right(user)
  
  // Call this method after validating user and length
  private def buildSession(user: User, length: Int): Session =
    val now = new Date()
    val endsAt = new Date(now.getTime + length * 1000)
    val sessionId = UUID.randomUUID
    val token = generateToken()
    Session(sessionId, token, now, endsAt, user)

  private def findActiveSessionByToken(token: String): Either[SessionError, Session] =
    sessionRepo.findActiveByToken(token) match
      case None => Left(InvalidToken(token))
      case Some(session) => Right(session)
}
