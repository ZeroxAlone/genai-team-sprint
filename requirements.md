# FX App — Ordered Requirements (the afternoon backlog)

*w2d1-a. A team race: ship as many of these as you can in the afternoon. Each requirement
is a **full feature** and a **branch → PR → merge to main**. The only rule that counts:*

> **A requirement is "done" ONLY when it works and is merged to `main`.**
> Works = a teammate ran the app and ticked every acceptance criterion against it.
> A merge that breaks `main` costs the whole team — so verify, then merge.

You start from the **running skeleton** (`docker compose up` already boots MySQL seeded with
seed-42 data, the Spring backend, and an empty web shell with a green `/health`). You are
*not* building plumbing — you're adding features onto a system that already runs. Use
Copilot to go fast (see `spec-first-method.md`); hand-code freely; the constitution
(`constitution.md`) is the always-true ruleset every feature must obey.

**How to read the table.** *Touches* tells you what files a feature is likely to change —
your coordination radar. Requirements that touch different areas can be built **in parallel**
by different people; ones that touch the same area (the home page, the router) will
**conflict** if you're not talking. *Depends on* tells you the order.

**One heads-up:** the sample `com.fx.sample` slice is **read-only** (a `SELECT`). Features
003 and 004 need to **write** — a `@PostMapping` that reads a request body and a
`jdbc.update("INSERT ...", ...)` in the repository. Same slice shape, just the write half;
your AI assistant knows the pattern — ask it, then verify the row actually landed
(`/api/health/db` counts, or a re-fetch).

---

## The scoreboard — six features

| # | Requirement | Value | Depends on | Touches | Parallel-safe? |
|---|---|---|---|---|---|
| 001 | Rates listing | Must | skeleton | backend + home page | starts everything |
| 002 | Single-pair lookup | Must | 001 | backend + home page | ⚠ shares home page w/ 001 |
| 003 | Conversion calculator | Must | 001 | backend + new convert page | ✅ new page |
| 004 | Record & list transfers | Must | 003 | backend + DB + history page | ✅ new page |
| 005 | Validation & error handling | Should | 002, 003 | backend (cross-cutting) | ⚠ touches many controllers |
| 006 | Account balance check | Should | 004 | backend + DB + convert page | ⚠ shares convert page w/ 003 |

Landing **001–004 working and merged** is a strong afternoon; 005–006 are the stretch. There
are more ideas under *If you fly through these* at the bottom — but **merged-and-working beats
started-and-broken**: a team with 4 solid merges outscores a team with 6 half-done branches
and a red `main`.

---

## The requirements

Each is written spec-style so you can hand it straight to Copilot (`spec-first-method.md`
shows the loop). **AC = acceptance criterion.** Numbers in **bold** are checkpoints — the AI
can't guess them, so they're stated; verify them exactly.

### 001 — Rates listing  *(Must · foundation)*
**Story.** As a customer I want to see the latest rate for each currency pair.
- AC1 `GET /api/rates` → 200 + JSON array; each item `{base, quote, rate, rateDate}`.
- AC2 **Exactly one row per pair** — the most recent `rateDate` (the table holds history).
- AC3 EUR/USD reads **rate = 1.0818**, **rateDate = "2026-01-12"**.
- AC4 The home page shows a table of every pair; EUR/USD row shows **1.0818**.
- AC5 Empty DB → `[]` and HTTP 200, never a 500.
**Done:** merged to `main`; the table renders with 1.0818 on a clean `docker compose up`.

### 002 — Single-pair lookup  *(Must)*
**Story.** As a customer I want the rate for one specific pair.
- AC1 `GET /api/rates/EUR/USD` → 200 + one object, **rate 1.0818**.
- AC2 Unknown pair (e.g. `EUR/XXX`) → **404**, a JSON message, no stack trace.
- AC3 Home page gains a "look up a pair" selector that shows the single rate.
**Coordinate:** touches the home page like 001 — agree who owns that file, or rebase.

### 003 — Conversion calculator  *(Must · the centrepiece)*
**Story.** As a customer I want to convert an amount and see the fee.
- AC1 `GET /api/convert?base=EUR&quote=USD&amount=100` → `{amount, rate, converted, fee, total}`.
- AC2 `converted` uses the latest rate: 100 EUR → **108.18** USD.
- AC3 `fee` uses **this morning's FeeCalculator tiers** (retail <1000 → 1.0%, 1000–9999 →
  0.5%, ≥10000 → 0.25%, min 1.00). 100 EUR → fee **1.08** (rounds off the 1% floor rule —
  pin your exact expected value in the spec before you generate).
- AC4 A **new** `/convert` page: pick pair, enter amount → shows converted + fee + total.
- AC5 Amount ≤ 0 → friendly error, no conversion.
**Parallel-safe:** new page + new endpoint — build alongside 002.

### 004 — Record & list transfers  *(Must)*
**Story.** Every conversion is recorded and visible as history.
- AC1 A conversion writes a `transfer` row (from, to, amount, currency, rate, timestamp).
- AC2 `GET /api/transfers` → array, **newest first**.
- AC3 A **new** `/history` page lists them, newest at top.
- AC4 Doing three conversions then loading history shows three rows in reverse order.
**Coordinate:** touches the DB schema — one person owns the migration/seed change.

### 005 — Validation & error handling  *(Must · cross-cutting)*
**Story.** The app never shows a stack trace to a customer.
- AC1 Bad input (missing param, non-numeric amount, unknown pair) → 4xx + a JSON `{error}`
  message; **never** a 500 with a stack trace.
- AC2 The UI shows the message inline, not a broken page.
- AC3 A `curl` of three deliberately-bad requests returns three clean messages.
**Coordinate:** touches many controllers — do it when 002/003 are merged, or you'll conflict
constantly. Good candidate for a pair to own together.

### 006 — Account balance check  *(Should)*
**Story.** A conversion is refused if the account can't cover it.
- AC1 Convert with `accountId` → if balance < amount, **refuse** with an insufficient-funds
  message (reuse Week-1's `InsufficientFundsException` semantics).
- AC2 A sufficient balance succeeds and **debits** the account.
- AC3 The convert page shows the balance and the refusal message.
**Coordinate:** shares the convert page with 003 — sequence after 003 merges.

---

## If you fly through these (unspecced — you write the spec)

Landed all six with time to spare? Pick one, **write the spec yourself** (that's the real
skill — see `spec-first-method.md`), and ship it the same way. Ideas:
- **Rate history for a pair** — `GET /api/rates/EUR/USD/history`, a simple line/list page.
- **Filter/search** the rates table by base or quote.
- **Refresh demo rates (fake tick)** — a `POST` that inserts new dated rows, so "latest per
  pair" visibly moves (a live proof of 001's grouping).
- **Polish & About** — consistent styling, an About page naming the team and its merged
  features.

---

## Definition of Done (every requirement, no exceptions)

- [ ] On a **branch** named `feat/00N-slug`, opened as a **PR** to `main`.
- [ ] A **teammate** ran `docker compose up` and **ticked every AC** against the running app.
- [ ] `main` still boots clean after the merge (nobody's feature is broken by yours).
- [ ] The PR description lists the ACs and says how they were checked.

*No CI gate today — CI arrives Wednesday. Today the gate is a human running the app. That's
the discipline CI will automate later: don't merge what you haven't seen work.*
