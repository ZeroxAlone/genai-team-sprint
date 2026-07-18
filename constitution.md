# FX App — Constitution

*The always-true rules for the whole app. Written once, obeyed by every feature. Paste this
into Copilot **above every feature spec** so the AI builds inside the guardrails. If a rule
here ever fights a feature spec, this file wins — or you change this file on purpose, as a
team, in its own PR.*

---

## Stack (don't swap without a team decision)
- **Backend:** Spring Boot 3.3.x, Java 21.
- **Database:** MySQL 8, schema `fxdb`, user `appuser`/`apppass`, seeded with the seed-42
  dataset (already in the skeleton). Access via JDBC (the skeleton shows the pattern).
- **Frontend:** plain HTML/CSS/JavaScript served by the app. No frontend framework, no build
  step, no heavy chart libraries — vanilla JS calling the REST API.
- **Run:** everything comes up with `docker compose up`. If a feature can't be demoed from a
  clean `docker compose up`, it isn't done.

## Frozen checkpoints (the AI cannot guess these — state them, verify them)
- EUR/USD on **2026-01-12** = **1.0818**. Any rates feature must reproduce this.
- **FeeCalculator tiers** (from w2d1 morning): retail `<1000` → 1.0%, `1000–9999` → 0.5%,
  `≥10000` → 0.25%, **minimum fee 1.00**; business flat 0.25%, min 5.00; negative amount
  throws. Any conversion feature charges by these.
- "Latest rate for a pair" = the row with the **max `rateDate`** for that `(base, quote)` —
  `fx_rate` holds history.

## API conventions
- REST under `/api/...`; JSON in and out.
- Money/rate fields are numbers with a sensible fixed scale; be consistent across endpoints.
- Field names are `camelCase` (`rateDate`, not `rate_date`) even though the DB is `snake_case`.
- Errors: **4xx with a JSON `{ "error": "message" }`** for bad input; **never a 500 with a
  stack trace to the browser**. A customer never sees a Java exception.

## Code conventions
- Package root `com.fx`. Controllers thin, logic in services, DB in repositories.
- Reuse the domain names from Weeks 1–2 (`FxRate`, `CurrencyConverter`, `FeeCalculator`,
  `Account`, `Transfer`, `InsufficientFundsException`) rather than inventing parallel ones.
- No secrets in code; config via the compose env the skeleton already sets.

## Definition of Done (every feature, enforced by review — see requirements.md)
- On a `feat/00N-slug` branch, opened as a PR to `main`.
- A **teammate ran the app** and ticked every acceptance criterion against it.
- `main` still boots clean on a **fresh clone** after the merge.
- No stack traces reach the browser; no `TODO`/dead code left in `main`.

## Scope guardrails (stop the AI gold-plating)
- Build **only** what the current feature spec's acceptance criteria ask for. If a spec
  doesn't mention auth, pagination, or a config screen, **don't add it.**
- Read-only where the spec is read-only. Don't invent write endpoints.
- Don't restructure or rename existing working code to build a new feature unless the spec
  says to — that's how you break someone else's merged feature.
