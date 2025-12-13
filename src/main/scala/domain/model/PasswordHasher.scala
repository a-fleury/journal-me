package com.journalme
package domain.model

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher:
  def hash(password: String): String =
    BCrypt.hashpw(password, BCrypt.gensalt())

  def verify(password: String, hash: String): Boolean =
    BCrypt.checkpw(password, hash)
