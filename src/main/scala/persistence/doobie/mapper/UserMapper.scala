package com.journalme
package persistence.doobie.mapper

import persistence.doobie.model.DbUser

import com.journalme.domain.model.{Genre, User}

object UserMapper {
  def toDomain(db : DbUser): User =
    User(
      id = db.id,
      firstName = db.first_name,
      lastName = db.last_name,
      email = db.email,
      genre = Genre.valueOf(db.genre)
    )
    
  def toDb(domain : User): DbUser =
    DbUser(
      id = domain.id,
      first_name = domain.firstName, 
      last_name = domain.lastName, 
      email = domain.email, 
      genre = domain.genre.toString
    )

}
