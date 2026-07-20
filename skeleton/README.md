# FX App — Skeleton (w2d1-a GenAI sprint)

A **running full-stack skeleton** your team grows into a working currency-exchange app this
afternoon. Everything already boots with one command — you add *features*, not plumbing.

```
┌── MySQL (fxdb, seeded) ──┐   ┌── Spring Boot backend ──┐   ┌── static front end ──┐
│ currency, account,       │──▶│ /api/currencies         │◀──│ index.html (Welcome) │
│ fx_rate, transfer        │   │ /health  /api/health/db │   │ currencies.html      │
└──────────────────────────┘   └─────────────────────────┘   └──────────────────────┘
                          the SAMPLE feature, end to end
```

## Run it (one command)

```bash
docker compose up --build
```

Then open:
- **http://localhost:8080/** — the Welcome page
- **http://localhost:8080/currencies.html** — the sample feature (a table from the DB)
- http://localhost:8080/health → `{"status":"UP"}`
- http://localhost:8080/api/health/db → status + table counts (`currency:8, account:20, fx_rate:30, transfer:200`)

First `up` takes a minute or two (it builds the image and MySQL runs the seed). Stop with
`Ctrl-C`; `docker compose down -v` resets the database.

> **Ports.** The app is on host **8080**. The containerised MySQL is published on host **3307**
> (not 3306) so it won't clash with the local MySQL you installed in Week 1 — point Workbench
> at `localhost:3307` (user `appuser`/`apppass`) if you want to inspect the data.

> **Run without Docker?** You need a local MySQL with `fxdb` seeded from `ops/fxdb-seed.sql`
> (user `appuser`/`apppass`), then `./mvnw spring-boot:run`. Docker is the easy path.
>
> **Windows/JAVA_HOME:** if Maven picks the wrong JDK, set `JAVA_HOME` to your JDK 21 for the
> session before `./mvnw` (see your Day-0 setup note). Docker sidesteps this entirely.

## What's given (the plumbing — don't rebuild it)
- `docker-compose.yml` + `Dockerfile` — MySQL + app, one command.
- `ops/fxdb-seed.sql` — the seed-42 dataset (EUR/USD 2026-01-12 = **1.0818**).
- `com.fx.sample.*` — the **sample feature**: `Currency` (model) → `CurrencyRepository`
  (JDBC) → `CurrencyController` (`/api/currencies`), plus `currencies.html` + `currencies.js`.
  **This is your copy-me pattern for every feature.**
- `com.fx.ops.*` — `/health` and `/api/health/db`.
- `com.fx.web.ApiExceptionHandler` — no stack traces to the browser (extend it later).
- `CurrencyControllerTest` — the web-slice test pattern (mocks the DB, runs in CI).
- `.github/workflows/ci.yml` — build + test on every push/PR.

## What you build
The features in **`../requirements.md`** — Rates, Convert, Transfer history, and more. Each
one is a branch → PR → merge, and it's *done only when it works and is merged to `main`*.
Follow **`../git-collaboration.md`** for the team setup and the per-feature loop, and
**`../spec-first-method.md`** for how to brief Copilot without getting burned. The always-true
rules are in **`../constitution.md`**; your AI assistant reads **`AGENTS.md`**.

## Make this your team's repo
```bash
# from inside this skeleton folder, after the owner created an EMPTY GitHub repo:
git init && git add -A && git commit -m "chore: fx-app skeleton"
git branch -M main
git remote add origin https://github.com/<owner>/fx-app-<team>.git
git push -u origin main
# then: Settings → Collaborators (add teammates) → Branches → protect main (PR + 1 review)
```

## Add a feature (the shape to copy)
1. Backend: a `record` model, a `*Repository` (JdbcTemplate), a `*Controller` (`/api/...`) —
   like `com.fx.sample`.
2. Front end: a new `*.html` page (copy `currencies.html`, wire it into the nav) + a `*.js`
   (copy `currencies.js`) that fetches your endpoint.
3. A web-slice test like `CurrencyControllerTest`.
4. Verify every acceptance criterion against the running app, open a PR, get it reviewed,
   merge. Pull `main`. Next feature.

*Tip: the greyed-out "Rates / Convert / History" links in the nav are where your first pages
slot in — un-grey them as you ship.*
