# demo-es-cqrs-axon

A small demo application illustrating Event Sourcing + CQRS with Axon Framework and Spring Boot.

Project structure (important packages)
- `commands` - command-side controllers, commands and aggregates
- `events` - domain events
- `query` - query-side controllers, entities, repositories and handlers

Running
1. Build and run with Maven:

```bash
./mvnw spring-boot:run
```

Or build a jar and run:

```bash
./mvnw -DskipTests package
java -jar target/demo-es-cqrs-axon-*.jar
```

By default the application runs on port 8080. Configure `src/main/resources/application.properties` for custom settings.

API Endpoints

This project exposes two main sets of REST endpoints: command endpoints (to modify state / publish commands) and query endpoints (to read projections and subscribe to updates).

Base URLs
- Commands: `/commands/accounts`
- Queries: `/query/accounts`

Command endpoints (write side)

1) Create an account
- POST /commands/accounts/add
- Request JSON body:
  {
    "initialBalance": 1000.0,
    "currency": "USD"
  }
- Response: CompletableFuture<String> — returns generated account id when completed

2) Credit an account
- POST /commands/accounts/credit
- Request JSON body:
  {
    "accountId": "<id>",
    "amount": 100.0,
    "currency": "USD"
  }
- Response: CompletableFuture<String>

3) Debit an account
- POST /commands/accounts/debit
- Request JSON body:
  {
    "accountId": "<id>",
    "amount": 50.0,
    "currency": "USD"
  }
- Response: CompletableFuture<String>

4) Update account status
- PUT /commands/accounts/updateStatus
- Request JSON body:
  {
    "accountId": "<id>",
    "accountStatus": "ACTIVE" // see AccountStatus enum
  }
- Response: CompletableFuture<String>

5) Read raw events from Event Store (debug)
- GET /commands/accounts/events/{accountId}
- Returns a stream of stored events for the given aggregate id

Query endpoints (read side)

1) Get all accounts
- GET /query/accounts/all
- Response: JSON array of `Account` projection objects

2) Get account statement (account projection + operations)
- GET /query/accounts/accouStatement/{accountId}
- Response: `AccountStatementResponseDTO` with the account and a list of `AccountOperation` objects

3) Watch account operations (server-sent events)
- GET /query/accounts/watch/{accountId}
- Produces `text/event-stream` SSE stream.
- The endpoint opens a subscription query that returns an initial result (empty or latest) and then streams updates (AccountOperation) whenever a debit/credit occurs for this account.

Subscription details
- The subscription query is implemented using Axon's SubscriptionQuery support in `AccountQueryController` and updates are emitted from the query-side `AccountEventHandler` via `QueryUpdateEmitter.emit`.
- The `watch` endpoint returns an `Flux<AccountOperation>` and uses the initial result concatenated with subsequent updates.

Messages / DTOs
- `Account` (projection): id, balance, status, currency, createdAt
- `AccountOperation`: id, date, amount, type (DEBIT/CREDIT), currency, account
- `AccountStatementResponseDTO`: { account, operations[] }

Troubleshooting
- If SSE updates are not received in the browser:
  - Ensure events are actually published on the command side (POST credit/debit succeeded).
  - Confirm `AccountEventHandler` saves the operation and calls `queryUpdateEmitter.emit(...)` with a predicate that matches the `WatchEventQuery`'s accountId and the update payload type matches the subscription's update type (AccountOperation).
  - In browsers like Chrome/Firefox, open devtools Network tab and check the `/query/accounts/watch/{id}` request to ensure it's kept open and that SSE data frames are received.
  - The SSE endpoint returns JSON-serialized AccountOperation objects; ensure the front-end parses them correctly.

Notes
- This project uses Axon Framework for command and query handling with Spring Boot.
- Entities and DTO classes are located in `src/main/java/net/anassploit/demoescqrsaxon/query` and `commands` packages.


