# MCP User Test Journey 🧪

This document describes a **typical end-to-end user journey** to manually test the JournalMe MCP server through an LLM (e.g. via Cursor).

The goal is to validate that **all MCP tools work correctly and coherently together**, from authentication to journaling queries and logout.

---

## Preconditions

* The JournalMe MCP server is running (`sbt run`)
* The MCP connection is correctly configured in Cursor
* The user interacts **only via natural language** (no direct API calls)
* The LLM is instructed to use the **JournalMe MCP tools** when relevant

## Note

* Adding your token to the prompt is optional, as long as you stay in the same conversation. However, including it explicitly helps ensure the correct session is used.
* If you token is expired, expect the following error message: `Error refreshing session.`

---

## Step 1 — Register

### User prompt

> I want to create an account. My email is `alice@example.com`, my password is `password123`, my first name is Alice and my last name is Smith.

### Expected MCP tool

* `register`

### Expected behavior

* A new user is created in the database
* The response confirms successful registration
* No authentication token is required yet

### Expected result (example)

> ✅ Account successfully created for Alice Smith.

---

## Step 2 — Login

### User prompt

> Log me in with email `alice@example.com` and password `password123`.

### Expected MCP tool

* `login`

### Expected behavior

* Credentials are validated
* A session is created
* A **session token** is returned and must be reused in all subsequent requests

### Expected result (example)

> ✅ Logged in successfully.
> Your session token is: `abc123-token`

---

## Step 3 — Add Journal Events

Add **2–3 journal events**, including at least one about going to a restaurant.

### Event 1 — Restaurant

#### User prompt

> Token : your-token
>
> Today I went to a restaurant with friends from 7pm to 9pm.

### Event 2 — Work

#### User prompt

> Token : your-token
>
> Yesterday I worked on my school project from 10am to 6pm.

### Event 3 — Optional extra event

#### User prompt

> Token : your-token
>
> Two days ago I went running in the park from 6pm to 7pm.

### Expected MCP tool

* `addJournalEvent`

### Expected behavior

* Dates, start times and end times are inferred or requested if missing
* Events are persisted in the database
* Each successful insertion is acknowledged

### Expected result (example)

> ✅ Journal event added successfully.

---

## Step 4 — Ask for a Weekly Summary

### User prompt

> Token : your-token
>
> Can you give me a summary of what I did this week?

*(or)*

> Token : your-token
>
> Can you summarize my activities from last week?

### Expected MCP tool

* `getEventsWithinPeriod`

### Expected behavior

* The MCP server retrieves all events within the requested time window
* Events are summarized in natural language

### Expected result (example)

> 📊 **Weekly Summary**
> This week, you worked extensively on your school project, went running in the park, and enjoyed a dinner at a restaurant with friends.

---

## Step 5 — Ask for the Last Time You Went to the Restaurant

### User prompt

> Token : your-token
>
> When was the last time I went to a restaurant?

### Expected MCP tool

* `getLastSimilarEvent`

### Expected behavior

* Events are searched semantically by title/description
* The most recent matching event is identified
* The result is explained clearly

### Expected result (example)

> 🍽️ You last went to a restaurant **today**, from 7pm to 9pm, with friends.

*(If no matching event exists, a clear message should be returned instead.)*

---

## Step 6 — Logout

### User prompt

> Token : your-token
>
> Log me out.

### Expected MCP tool

* `logout`

### Expected behavior

* The current session is invalidated
* The token can no longer be used

### Expected result (example)

> 👋 You have been logged out successfully.

---

**End of test journey** 🎉
