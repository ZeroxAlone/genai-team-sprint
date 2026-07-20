# Working Spec-First with AI — *the fast way to brief Copilot without getting burned*

*w2d1-a. A recommended technique, not a rule. You're allowed to hand-code — but when you let
the AI write a whole feature, this loop is how you go fast **and** stay in control. It's the
thing you actually teach in the 15 theory minutes; the rest of this folder is scaffolding
around the loop on this page.*

> **The one idea.** Copilot can write most of this app for you — endpoints, queries, even the
> Spring wiring you haven't been taught yet. Your job is to **describe the behaviour
> precisely enough that it gets it right**, and **catch it when it doesn't.** That's the same
> *trust-but-verify* habit you set on Day 1, now scaled from one method to a whole
> application.

This is inspired by GitHub's **Spec Kit**, boiled down to something a graduate can hold in
their head after one demo. No CLI to install, no new tool to learn — just **markdown files
+ Copilot + one loop.**

---

## Hand-code or spec? Both are allowed — here's when each wins

You can type in `src/` whenever you want. Two honest guidelines:

- **Let the AI write the feature** when it's boilerplate-heavy or uses stuff you haven't
  learned yet (a Spring endpoint, a JDBC query). That's most of today — it's *how far can
  GenAI carry us* that we're measuring.
- **Reach for the keyboard** for a two-line glue fix, a rename, or when you understand the
  code better than you can re-spec it. Re-generating a whole feature to fix a typo is silly.

The **one thing that's non-negotiable** isn't "don't touch code" — it's this:

> **When the AI's output is wrong, first ask: *was my brief unambiguous?* Usually it wasn't.
> Fixing the spec and regenerating teaches you more than patching the symptom — and it's the
> habit that scales.** *Your output quality tracks your spec quality.*

Garbage rates on the page? Your brief didn't pin the number. Duplicate rows? It didn't say
"latest per pair." You *can* hand-fix it — but if you fix the spec instead, the next person
who regenerates gets it right too, and you've learned what precise looks like.

**Infrastructure is given, so you're never stuck on plumbing.** `docker compose up` already
starts MySQL (seed-42), the Spring backend, and an empty web shell. You're adding *features*
onto a running system — not fighting Docker or JDBC, which isn't the lesson today.

---

## The four artifacts

Plain markdown, lives in the repo, versioned like code.

| Artifact | How many | Who writes it | What it pins down |
|---|---|---|---|
| **`constitution.md`** | one, up front | whole team, once | The *always-true* rules: stack, conventions, Definition of Done, the frozen checkpoints (EUR/USD 1.0818, the FeeCalculator tiers). The AI reads this **every** time. |
| **`specs/00N-name.md`** | one per feature | the feature's author | *This* feature's behaviour: story, acceptance criteria, the API/UI contract, what's out of scope. |
| **the branch** | one per spec | git | `feat/001-rates-listing` — the spec and its generated code travel together to a PR. |
| **the PR** | one per feature | reviewer | Where a teammate checks the code **against the acceptance criteria** — not against their taste. |

You don't need a "plan" or "tasks" file like full Spec Kit does — for a feature this size,
you'll ask Copilot to *propose* a plan in chat, you'll approve it, and it implements. Keep
the ceremony to what earns its place in an afternoon.

---

## The loop (this is the lesson)

Four steps. Every feature. Every branch.

```
   ┌──────────────────────────────────────────────────────────┐
   │                                                          │
   ▼                                                          │
 SPECIFY  ──►  GENERATE  ──►  VERIFY  ──►  correct? ──yes──►  merge (PR)
 (human)       (Copilot)      (human)         │
                                              no
                                              │
                                     REFINE THE SPEC ─────────┘
                                     (prefer this over patching src/)
```

1. **Specify** *(human, ~15 min).* Fill the spec template for one small feature. The skill
   is here — see *What makes a spec good* below.
2. **Generate** *(Copilot).* New branch. Paste **constitution + spec** into Copilot Chat,
   ask it to propose a plan, approve, let it implement. (Prompt recipe below.)
3. **Verify** *(human, ~10 min).* Run it. Walk **every acceptance criterion** and tick it
   off against the real running app. Does EUR/USD actually read **1.0818**? This is where
   the exercise lives or dies — a criterion you don't check is a criterion the AI is free to
   get wrong.
4. **Refine.** If a criterion fails, ask **one question: *was my spec unambiguous about
   this?*** Almost always the answer is no. Sharpen the spec (add the missing criterion, pin
   the missing number), and **regenerate**. If the spec truly was clear and the AI still
   erred, add the failing case to the spec as an explicit criterion and tighten the prompt.
   **Prefer fixing the spec over hand-patching the file** — same fix, but the next
   regeneration keeps it, and you've learned what your brief was missing. (A trivial glue
   fix by hand is fine; re-speccing a whole feature to fix a typo is not.)

The git history that falls out — spec commit, generated commit, refine commit, merge —
*is* the deliverable. It's the evidence the team worked spec-first.

---

## What makes a spec good (the coaching checklist)

This is the half-page you put on the projector. A good feature spec is:

- **Testable, not wishful.** "Show a nice rates page" can't be verified — so the AI can't be
  wrong, which means it can't be right either. Write **Given / When / Then** with **exact
  expected values**. `Given the seeded DB, When I GET /api/rates, Then EUR/USD on 2026-01-12
  reads 1.0818.` *That* the AI can hit or miss.
- **Numbers, pinned.** The AI cannot guess 1.0818 or your fee tiers. If a value matters,
  **state it in the spec.** Every checkpoint from this course belongs in the acceptance
  criteria verbatim.
- **Bounded.** Say what's **out of scope** or the AI will gold-plate — auth you didn't ask
  for, five endpoints when you wanted one. A spec that doesn't say "no" says "surprise me."
- **One feature, one branch.** Small enough to *review in ten minutes*. If your spec has
  more than ~5 acceptance criteria, it's two features.
- **Contract, not implementation.** Specify the **observable shape** — the JSON the endpoint
  returns, the elements on the page — and let the AI choose the classes and methods. The
  moment your spec says "create a `RatesController` with a method `findAll`," you're
  hand-coding in prose. Describe the *what*; the constitution constrains the *how* enough.

Four anti-patterns to name out loud so they can catch each other:

| Anti-pattern | Smell | Fix |
|---|---|---|
| **The wish** | "make it user-friendly" | replace with a checkable criterion |
| **The novel** | 3 pages, 12 criteria | split into features/branches |
| **The how-spec** | dictates class & method names | specify behaviour + contract instead |
| **Silent accept** | PR merged, criteria never ticked | the reviewer runs the app and checks each one |

---

## Worked example — Feature 001: Rates listing

The first real vertical slice. The skeleton already serves an empty page and a seeded DB;
001 turns that into "browse the latest rate for each pair." Here is the **whole loop, start
to finish** — the spec, the exact prompt, the verification, and a realistic miss.

### ① The spec (`specs/001-rates-listing.md`)

```markdown
# 001 — Rates listing

## Story
As a customer, I want to see the latest exchange rate for each currency pair,
so that I know today's price before I convert.

## Acceptance criteria
- AC1  Given the seeded fxdb, When I GET /api/rates,
       Then I receive HTTP 200 and a JSON array.
- AC2  Each element has exactly: base (string), quote (string),
       rate (number), rateDate (string, YYYY-MM-DD).
- AC3  The response contains EXACTLY ONE row per base/quote pair —
       the one with the most recent rateDate.
- AC4  The EUR/USD element reads rate = 1.0818 with rateDate = "2026-01-12".  ← checkpoint
- AC5  Given I open the web app's home page,
       Then a table lists every pair with its rate and date, EUR/USD showing 1.0818.
- AC6  Given the DB is empty, When I GET /api/rates,
       Then I receive HTTP 200 and an empty array [] (never a 500, never a stack trace).

## Contract
GET /api/rates  ->  200
[ { "base":"EUR", "quote":"USD", "rate":1.0818, "rateDate":"2026-01-12" }, ... ]

## Out of scope
No pair filtering, no historical rows, no pagination, no auth. Read-only. (Those are 002+.)
```

Notice: six criteria, one pinned number, an explicit empty-DB case, an explicit *out of
scope*. That took ~15 minutes and it's the entire "engineering" a student does for 001.

### ② Generate (paste into Copilot Chat, on branch `feat/001-rates-listing`)

```
You are implementing one feature in our FX app. Obey CONSTITUTION.md as hard
constraints (stack, conventions, Definition of Done). Implement ONLY the spec below.

First: propose a short plan (files you'll add/change, the query, the endpoint, the
UI change). Wait for my "go". Do not gold-plate beyond the acceptance criteria; respect
the Out of scope list. Then implement, and tell me exactly how to run and verify each
acceptance criterion.

<paste CONSTITUTION.md>
<paste specs/001-rates-listing.md>
```

Approve the plan, let it implement, `docker compose up`.

### ③ Verify (tick every criterion against the running app)

| AC | How you check it | Pass? |
|---|---|---|
| AC1 | `curl localhost:8080/api/rates` → 200, array | ☐ |
| AC2 | eyeball one element's keys/types | ☐ |
| AC3 | count rows; no pair appears twice | ☐ |
| AC4 | find EUR/USD → `1.0818`, `2026-01-12` | ☐ |
| AC5 | open the page → table renders, EUR/USD row shows 1.0818 | ☐ |
| AC6 | (stretch) point at empty DB → `[]`, not a 500 | ☐ |

### ④ A realistic miss → refine the spec (the teachable moment)

Run it and AC3 fails: the array has **several EUR/USD rows**, one per historical date.
Instinct says "open the repository and add a `GROUP BY`." **Don't.** Ask the loop's
question: *was my spec unambiguous?* Look again — AC3 says "exactly one row per pair, the
most recent." It's there... but the AI read "list the rates" (AC1) as the dominant
instruction and treated AC3 as a nice-to-have. The spec was *present* but not *emphatic*.

Fix the **spec**, not the code:

```markdown
- AC3  The response contains EXACTLY ONE row per base/quote pair — the one with the
       MAX(rateDate). A pair appearing twice is a FAILURE. (fx_rate holds history;
       "latest" means group by (base,quote), keep the newest.)
```

Re-paste, regenerate, re-verify. AC3 goes green. Commit: `refine(001): pin latest-per-pair`.
The student just learned, in ninety seconds, that **the ambiguity in their head became a
bug on the screen** — and that the fix lives in the spec. That lesson is the entire
afternoon in miniature.

---

## Why this beats "just vibe-code it with Copilot"

Because a spec makes *wrong* a visible, catchable event. Without acceptance criteria,
"the AI wrote something and it kind of works" is the ceiling — nobody can say whether
EUR/USD is 1.0818 or 1.0800, because nobody wrote down that it should be 1.0818. The spec
is what lets a room full of graduates **grade a machine's work** — which is the single most
important skill this course is trying to leave them with.
