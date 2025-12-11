package com.journalme
package domain.model

import java.util.UUID


final case class User(
  id: UUID,
  firstName: String,
  lastName: String,
  email: String,
  genre: Genre
)





