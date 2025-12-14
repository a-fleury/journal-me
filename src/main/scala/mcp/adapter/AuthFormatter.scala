package com.journalme
package mcp.adapter

import domain.model.User
import mcp.schema.LoginResponse

object AuthFormatter {
  
  def loginSuccess(response: LoginResponse): String = {
    s"""
       |Login Successful!
       |
       |User : ${response.user.toString}
       |  - ID: ${response.user.id}
       |  - Email: ${response.user.email}
       |  - Genre: ${response.user.genre}
       |  - First Name: ${response.user.firstName}
       |  - Last Name: ${response.user.lastName}
       |
       |Session : 
       |  - Token: ${response.session.token}
       |  - Starts At: ${response.session.startsAt}
       |  - Ends At: ${response.session.endsAt}
       |  
       |Everytime the user needs to make a request that requires authentication, they need to provide the token in the prompt.
       |Everytime the user makes an authenticated request, the session will be refreshed, the token will stay the same but the expires_at will be updated.
       |The user can now continue their journaling adventure!
       |""".stripMargin
  }
       
  def registerSuccess(response: User): String = {
    s"""
       |Registration Successful!
       |
       |User :
       |  - ID: ${response.id}
       |  - Email: ${response.email}
       |  - Genre: ${response.genre}
       |  - First Name: ${response.firstName}
       |  - Last Name: ${response.lastName}
       |  
       |The user has been created successfully and can now login to start their amazing journaling adventure!
       |""".stripMargin
  }

  def logoutSuccess(): String =
    s"""
       |Logout Successful!
       |The session has been ended successfully.
       |The user will need to login again to continue their journaling adventure.
       |""".stripMargin

}
