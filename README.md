# fx-w2d1-a — GenAI Code Sprint (afternoon)

A 2.5–3h team session: **how fast and how far can a team ship real FX features when an AI
writes most of the code — and can you keep everyone in sync through one `main` branch?**
Runs *alongside* the curriculum (Spring/JDBC/REST are taught properly on later days); today
is about **speed, coordination, and verifying the machine's work.** It's also a rehearsal for
the Week-3 neo-capstone, which uses the same skeleton-plus-specs shape.

**What today is not:** it is not the hand-built fx-api capstone (that's still built topic-by-
topic later). Nothing here replaces the canonical `capstone-spec.md`.

## The setup
Teams start from the **running skeleton** in `skeleton/` (`docker compose up` → seeded MySQL +
Spring backend + a web front end with a Welcome page and one working sample feature). They add
features onto it, one branch → PR → merge each. The only hard rule: **a feature is done only
when it works and is merged to `main`.**

## Files (read in this order)

| File | What it's for | Audience |
|---|---|---|
| **`run-sheet.md`** | The afternoon's beats, timeboxes, talking points. Start here. | instructor |
| **`spec-first-method.md`** | The 15-min theory: brief→generate→verify→refine loop + one fully-worked feature. The technique for going fast with Copilot. | both |
| **`constitution.md`** | The always-true ruleset every feature obeys; paste above every spec. | teams |
| **`requirements.md`** | The ordered backlog — **6 full features**, each a branch/PR/merge, with acceptance criteria + checkpoints (+ stretch ideas). **The thing they race.** | teams |
| **`git-collaboration.md`** | The team git playbook: repo setup, the per-feature loop, resolving the conflict, coordination patterns. **The real skill under test.** | teams |
| **`skeleton/`** | The running full-stack starting repo teams clone (Docker + MySQL seed + Spring + HTML/JS + the sample Currencies feature). Its own `README.md` has run + push steps. | teams |
| **`progress-tracker.csv`** | Per-team progress board (opens in Excel). Each team copies one and hand-edits status as features merge. | teams |
| `specs/` | Where teams drop their own per-feature spec files as they go. | teams |

## The one-line pitch to the room
> This morning you proved code with tests. This afternoon you find out how fast a *team*
> ships when an AI writes the code and git keeps you in sync — and whether you can catch the
> AI when it's confidently wrong. **Working-and-merged is the only score that counts.**

## Before you run this for real (local verification)
The build sandbox had no JDK/Maven/Docker, so the skeleton is correct-by-construction but
**not yet executed**. Once, on a machine with Docker + JDK 21:
- `cd skeleton && docker compose up --build` → open `localhost:8080/` (Welcome) and
  `/currencies.html` (8 currencies); check `/api/health/db` shows the seeded counts.
- `cd skeleton && ./mvnw test` → the sample `CurrencyControllerTest` passes (no DB needed).
- Push `skeleton/` to a throwaway GitHub repo and confirm the CI workflow goes green.
# fx-w2d1-a
