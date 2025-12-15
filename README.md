# JournalMe 📔

A fun AI-powered journaling application that makes tracking your life events as easy as having a conversation. Talk to an AI to log events, summarize your day or week, and get reminders about when you last did something.

Note: This is a school project with a playful twist on authentication - you can literally tell the LLM your credentials and it will log you in! 🎉 (Not recommended for production use, obviously!)

## Features

* 🗣️ **Conversational Journaling** - Simply talk to the AI to record your daily events
* 📊 **Smart Summaries** - Get AI-generated summaries of your day, week, or custom time periods
* 🔔 **Activity Reminders** - Ask when you last did something and get instant answers
* 🎭 **Fun Authentication** - Just tell the AI your email and password, and it handles the login! No headers, you just add the token to your prompt!

### Tools available

**Authentication**:
- Register
- Login
- Logout

**Journaling**:
- Add a journal event
- Summarize events for a chosen period
- Know when you last did something


## Tech Stack

* **Scala 3.3.7** — Modern, strongly-typed functional programming language
* **Cats Effect 3.6.3** — Purely functional effect system for async, concurrency, and resource safety
* **Chimp (MCP)** `v0.1.6` — Model Context Protocol server framework for LLM tool integration
* **Tapir** `v1.13.3` — Type-safe endpoint definitions and request/response modeling
* **Tapir Netty Cats Server** — Effectful HTTP server powered by Netty and Cats Effect
* **Netty** — High-performance asynchronous networking framework
* **Doobie** `v1.0.0-RC11` — Functional JDBC layer for database access

    * PostgreSQL integration
    * HikariCP connection pooling
* **PostgreSQL** — Relational database for persistence
* **Flyway** — Database schema migrations (via Docker)
* **Logback** — Structured application logging
* **jBCrypt** — Secure password hashing

### Testing & Tooling

* **ScalaTest** — Unit and integration testing
* **cats-effect-testing** — Effect-aware test utilities
* **Testcontainers (PostgreSQL)** — Ephemeral, reproducible database testing
* **Docker & Docker Compose** — Local infrastructure (Postgres + Flyway)
* **sbt** — Build and dependency management

## Architecture

### Project Structure

```
JournalMe/
├── src/              # Source code
├── project/          # sbt build configuration
├── target/           # Compiled artifacts (ignored)
├── build.sbt         # Project dependencies and settings
└── .env              # Environment variables (ignored)
```

JournalMe follows a **clean, layered architecture** with a strong emphasis on **functional effects (`IO`) from edge to persistence**.

### High-level Layers

```
┌─────────────────────┐
│        MCP          │  ← Chimp tools exposed to the LLM
│  (tools / adapters) │
└─────────▲───────────┘
          │
┌─────────┴───────────┐
│      Services       │  ← Business logic, validation, orchestration
│   (pure + IO)       │
└─────────▲───────────┘
          │
┌─────────┴───────────┐
│    Persistence      │  ← Doobie repositories (PostgreSQL)
│   (IO, SQL)         │
└─────────▲───────────┘
          │
┌─────────┴───────────┐
│     Database        │  ← PostgreSQL
└─────────────────────┘
```

### Package Responsibilities

* **domain**
  Core business models and error types. Free of infrastructure concerns.

* **persistence**
  Doobie repositories, SQL mappings, and database access. All repositories return `IO`.

* **service**
  Application logic and orchestration. This layer coordinates repositories, applies validation rules, and returns domain results wrapped in `IO`.

* **mcp**
  Chimp tools exposed to the LLM. This layer adapts structured domain results into **LLM-friendly strings**.

* **mcp.adapter / formatter**
  Pure formatting logic that converts domain objects and errors into natural language responses for the LLM.

### Design Principles

* **IO everywhere** — All side effects are explicit and controlled
* **Separation of concerns** — Formatting, business logic, and persistence are clearly isolated
* **Type safety first** — Errors are modeled explicitly using sealed traits
* **LLM-friendly boundary** — Only the MCP layer deals with strings and prompts

## Setup Instructions

### Prerequisites

* Java SDK 21
* sbt (Scala Build Tool)
* [Cursor IDE](https://cursor.sh/) - Required for using JournalMe's MCP integration

### Installation

1. **Clone the repository**

```bash
git clone <your-repo-url>
cd JournalMe
```

2. **Set up environment variables**

Create a `.env` from the `.env.example` file in the project root.

The project injects environment variables via the system.
Either configure the IDE to use the `.env` file or export the variables manually.

```bash
cp .env.example .env
# Then edit the .env file with your preferred settings
```

**Or,**

```bash
export SESSION_LENGTH=<length_in_seconds>
export POSTGRES_PORT=<port>
export POSTGRES_USER=<user>
export POSTGRES_PASSWORD=<pwd>
```

3. **Build the project**

```bash
sbt compile
```
4. **Launch the database**

Ensure the port configuration fits your local environment

You can connect the datasource to your IDE of choice if you want to see its structure
```bash
#This will also trigger a flyway migration
docker compose up
```
**Or,**

```bash
make db-up
make db-migrate
```
5. **Run the application**

```bash
sbt run
```

### Using with Cursor IDE

Since JournalMe uses Chimp for MCP (Model Context Protocol), you'll need to use Cursor IDE to interact with the JournalMe MCP server in your AI chat sessions.

* Open the project in Cursor
* Configure the MCP connection in Cursor's settings
* Start chatting with the AI and use JournalMe's capabilities!
* In your first prompt, do not hesitate to specifically ask the LLM to use the JournalMe MCP server

## Development Notes

* The `.bloop`, `.bsp`, and `.metals` directories are generated by Metals (Scala language server)
* Build artifacts are automatically ignored via `.gitignore`
* Make sure your `.env` file is never committed to version control

## Contributing

This is a school project, but feel free to experiment and have fun with it! Just remember: the authentication method is intentionally silly - please don't use this pattern in real applications! 😄

**Happy Journaling! ✨**