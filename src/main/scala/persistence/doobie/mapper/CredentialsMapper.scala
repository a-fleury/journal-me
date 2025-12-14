package com.journalme
package persistence.doobie.mapper

import persistence.doobie.model.DbCredentials
import domain.model.Credentials

object CredentialsMapper {
  def toDomain(db: DbCredentials): Credentials = {
    Credentials(
      userId = db.user_id,
      passwordHash = db.password_hash
    )
  }
    
    def toDb(domain: Credentials): DbCredentials = {
      DbCredentials(
        user_id = domain.userId,
        password_hash = domain.passwordHash
      )
    }

}
