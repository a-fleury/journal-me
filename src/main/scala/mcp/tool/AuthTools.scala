package com.journalme
package mcp.tool

import domain.error.UserError
import domain.service.{AuthService, SessionService}
import domain.model.Session
import mcp.schema.{Authenticated, LoginRequest, LoginResponse, RegisterRequest}

import cats.effect.IO
import chimp.{ServerTool, ToolAnnotations, tool}
import com.journalme.mcp.adapter.AuthFormatter

final class AuthTools(
  authService: AuthService
) {

  private val basicInstructions = "If you don't have every single information needed for the input, ask the user to provide them. Always provide a valid JSON object, not a string, matching the exact schema. ALL DATES MUST MATCH java.util.Instant format. If userPrompt is part of the schema, it means you need to provide the question asked by the user."

  private val basicInstructionsWithAuth = basicInstructions + " " + "This endpoint needs authentication, the user must provide a token."

  val register: ServerTool[RegisterRequest, IO] = tool("register")
    .description(basicInstructions + " " + "If you don't have every single information needed for the input, ask the user to provide them. Register a new user. If successful, returns a message with the user indicating that the registration was successful. If not successful, returns an error indicating what went wrong.")
    .input[RegisterRequest]
    .serverLogic[IO] {
      (request, headers) => handleRegister(request)
    }

  val login: ServerTool[LoginRequest, IO] = tool("login")
    .description(basicInstructions + " " + "If you don't have every single information needed for the input, ask the user to provide them. Login a user that provided a valid email and password. If successful, returns a message with the user and a session with a token they will need to provide in every request that needs authentication. If not successful, returns an error indicating what went wrong.")
    .input[LoginRequest]
    .serverLogic[IO] {
      (request, headers) => handleLogin(request)
    }

  val logout: ServerTool[Authenticated, IO] = tool("logout")
    .description(basicInstructionsWithAuth + " " + "If you don't have every single information needed for the input, ask the user to provide them. Logout a user that provided a valid token. If successful, returns a message indicating that the logout was successful. If not successful, returns an error indicating what went wrong.")
    .input[Authenticated]
    .serverLogic[IO] {
      (request, headers) => handleLogout(request)
    }


  private def handleRegister(request: RegisterRequest): IO[Either[String, String]] =
    authService.register(request).map {
      case Right(user) => Right(AuthFormatter.registerSuccess(user))
      case Left(err)   => Left(s"Error during registration: ${err.message}")
    }

  private def handleLogin(request: LoginRequest): IO[Either[String, String]] =
    authService.login(request).map {
      case Right(response) => Right(AuthFormatter.loginSuccess(response))
      case Left(err)      => Left(s"Error during login: ${err.message}")
    }

  private def handleLogout(request: Authenticated): IO[Either[String, String]] =
    authService.logout(request).map {
      case Right(_)  => Right(AuthFormatter.logoutSuccess())
      case Left(err) => Left(s"Error during logout: ${err.message}")
    }

}
