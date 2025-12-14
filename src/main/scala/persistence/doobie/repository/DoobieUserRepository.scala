package com.journalme
package persistence.doobie.repository

import domain.model.User
import persistence.doobie.Database
import persistence.doobie.mapper.UserMapper
import persistence.doobie.model.DbUser
import persistence.repository.UserRepository

import cats.effect.{IO, Resource}
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*

import java.util.UUID


final class DoobieUserRepository private[persistence] (
  xa: Transactor[IO]
) extends UserRepository {
  
  private val selectUser =
    fr"""
        SELECT 
        id, first_name, last_name, email, genre 
        FROM users
        """

  override def save(user: User): IO[User] = {
    val dbUser = UserMapper.toDb(user)
    
    sql"""
         INSERT INTO users 
         (id, first_name, last_name, email, genre) 
         VALUES
          (${dbUser.id}, ${dbUser.first_name}, ${dbUser.last_name}, ${dbUser.email}, ${dbUser.genre})
    """
      .update
      .run
      .transact(xa)
      .as(user)
  }

  override def findByEmail(email: String): IO[Option[User]] = {
    (selectUser ++ fr"WHERE email = $email")
      .query[DbUser]
      .option
      .map(_.map(UserMapper.toDomain))
      .transact(xa)
  }

  override def findById(id: UUID): IO[Option[User]] = {
    (selectUser ++ fr"WHERE id = $id")
      .query[DbUser]
      .option
      .map(_.map(UserMapper.toDomain))
      .transact(xa)
  }
}

object DoobieUserRepository {
  def resource: Resource[IO, DoobieUserRepository] =
    Database.transactor.map(new DoobieUserRepository(_))
}
