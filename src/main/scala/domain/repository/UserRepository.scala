package com.journalme
package domain.repository

import domain.model.{Credentials, User}

import java.util.UUID

trait UserRepository {
  def save(user: User): User
  def delete(id: UUID): Boolean
  def exists(id: UUID): Boolean
  def findById(id: UUID): Option[User]
  def findByEmail(email: String): Option[User]
  def findCredentials(email: String): Option[Credentials]
}
