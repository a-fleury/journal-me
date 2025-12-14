package com.journalme
package persistence.doobie.mapper

import persistence.doobie.model.{DbSession, DbUser}
import domain.model.Session

object SessionMapper {
  def toDomain(dbSession: DbSession, dbUser: DbUser): Session =
    val user = UserMapper.toDomain(dbUser)
    Session(
      id = dbSession.id,
      token = dbSession.token,
      startsAt = dbSession.starts_at,
      endsAt = dbSession.ends_at,
      user = user
    )
    
  def toDb(domain: Session): DbSession =
    DbSession(
      id = domain.id,
      token = domain.token,
      starts_at = domain.startsAt,
      ends_at = domain.endsAt,
      user_id = domain.user.id
    )

}
