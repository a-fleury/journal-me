package com.journalme
package persistence.doobie.repository

import domain.model.Credentials
import persistence.doobie.mapper.CredentialsMapper
import persistence.doobie.model.DbCredentials
import persistence.repository.CredentialsRepository

import cats.effect.{IO, Resource}
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*

import java.util.UUID

final class DoobieCredentialsRepository private (
  xa: Transactor[IO]
) extends CredentialsRepository {

  private val selectCredentials =
    fr"""
      SELECT user_id, password_hash 
      FROM credentials 
      """

  override def save(credentials: Credentials): IO[Credentials] = {
    val dbCredentials = CredentialsMapper.toDb(credentials)
    
    sql"""
       INSERT INTO credentials
        (user_id, password_hash) 
        VALUES 
        (${dbCredentials.user_id}, 
        ${dbCredentials.password_hash})
       """
      .update
      .run
      .transact(xa)
      .as(credentials)
  }

  override def findByUserId(userId: UUID): IO[Option[Credentials]] = {
    (selectCredentials ++ 
      fr"""
           WHERE user_id = $userId
        """
      )
      .query[DbCredentials]
      .option
      .map(_.map(CredentialsMapper.toDomain))
      .transact(xa)
    
  }
  
}
