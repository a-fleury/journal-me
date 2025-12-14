package com.journalme
package persistence

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class PostgresTestContainer
  extends PostgreSQLContainer[PostgresTestContainer](
    DockerImageName.parse("postgres:16-alpine")
  )

