# w2d1-a — Run Sheet (afternoon, ~2.5–3h)

*GenAI for code + team git sprint. Instructor beats, timeboxes, talking points. Runs
alongside the curriculum — Spring/JDBC/REST are taught properly later; today is about
**speed, coordination, and shipping working features with AI**.*

**Prereqs on the machines:** the skeleton clones and `docker compose up` boots (MySQL seed-42
+ Spring + web shell + green `/health`); GitHub access confirmed; Copilot signed in in VS
Code (or their own agent). Flag the JAVA_HOME/session gotcha up front.

**The framing sentence to open with:**
> "This morning you proved code with tests. This afternoon you find out how fast a *team*
> can ship real features when an AI writes most of the code — and whether you can keep four
> people out of each other's way through one `main` branch. Trust-but-verify, scaled to a
> whole app."

---

## Beat 0 — Theory: GenAI for code, honestly (~15 min, slides)

Keep it to 15 minutes — they learn the rest by doing.
- **What Copilot is good at:** boilerplate, a whole endpoint from a clear description, wiring
  you haven't learned yet (it'll write Spring/JDBC before Wednesday teaches it — *that's the
  point of today*), tests, explaining unfamiliar code.
- **Where it burns you:** confidently wrong numbers, silent scope creep, code that compiles
  but does the wrong thing, subtly breaking something else. **It is a fast junior, not an
  oracle.**
- **The one habit:** *describe precisely, then verify against what you expected.* You already
  did this to `FeeCalculator` this morning (code-vs-rules). Today you do it to a whole app.
- Hand off to `spec-first-method.md` — the brief→generate→verify loop — as the recommended
  way to go fast without getting burned. **Note it's a technique, not a cage:** hand-coding
  is fine; the loop is just what keeps AI speed from becoming AI debt.

**Talking point:** "The measure of a good engineer this afternoon isn't who types least —
it's who catches the AI being wrong. A wrong rate on the page that nobody noticed is worse
than a feature you didn't build."

---

## Beat 1 — Teams + repo up (~20 min, hands-on)

Everyone follows `git-collaboration.md` Part 1. Circulate and unblock — this is where the
afternoon is won or lost. **Do not let anyone start a feature until:**
- the team repo exists, collaborators **accepted**, `main` branch-protected (PR + 1 review),
- everyone has cloned and **seen `docker compose up` boot green** on their own machine,
- the requirements board is claimed (names on rows).

**Common stalls:** collaborator invites not accepted (check spam / GitHub notifications);
JAVA_HOME→wrong JDK (set it for the session); Docker Desktop still starting; branch
protection blocking the owner's own first push (that's expected — the skeleton went up
*before* protection was on, or push via a PR).

---

## Beat 2 — Mob the foundation: 001 together (~30 min, hands-on)

Whole team, one screen (or one driver, rotate): build **001 Rates listing** using the
spec-first loop, live. This is the worked example in `spec-first-method.md` — including the
**planted miss** (AI returns duplicate EUR/USD rows → they fix the *spec's* "latest per pair"
criterion, not the code). Merge 001 to `main` through a real PR + review. Everyone pulls.

**Why mob it:** it de-risks the loop (everyone's seen it once), it touches the shared home
page (so doing it together avoids the first conflict), and it puts a working feature on
`main` inside the first hour — momentum.

**Checkpoint to call across the room:** "Everyone's `main` shows EUR/USD **1.0818** on a
fresh `docker compose up`? Good. Now fan out."

---

## Beat 3 — The race: fan out on the backlog (~60–80 min, hands-on)

Teams self-organise off `requirements.md` + `git-collaboration.md` Part 4. Members claim
requirements, branch, build with Copilot, verify, PR, review, merge. Your job:
- **Run a 30-sec stand-up every ~40 min** across all teams: "what did you merge, what's next,
  who's touching the shared pages?"
- **Engineer one conflict per team.** If a team is avoiding conflicts by working too
  serially, nudge two people onto 002 and 008 (both touch the home page) at once — then coach
  the resolution (`git-collaboration.md` Part 3). **A resolved conflict is a graded outcome.**
- **Watch for the anti-patterns:** merging without running (call it out — "did you tick the
  ACs?"), a broken `main` (stop-the-line, `git revert`), hoarded long-lived branches
  (push them to merge something small now).

**Talking point when a team hits a confidently-wrong AI output:** "There it is. It compiled,
it looked right, the number was wrong. That's the whole afternoon in one moment — who
verifies is who ships."

---

## Beat 4 — Show & tell + retro (~20 min)

Each team, 3 min:
- `git log --oneline main` on the projector — read the merged features aloud.
- `docker compose up` a **fresh clone** → demo the merged features live. (Fresh clone is the
  honesty check: does `main` actually work, or just someone's laptop?)
- One sentence each: **the best thing GenAI did** and **the worst thing it tried to do**.

Retro questions:
- Where did coordination cost you time? What would you set up differently in the first 20 min?
- Did anyone hand-code faster than they could spec it? When is each right?
- How many features would you have shipped *without* the AI? *With* it but *without* git
  discipline (i.e. one shared branch, no PRs)?

**Close the loop to the week:** "This is exactly how the Week-3 neo-capstone runs — skeleton,
specs, parallel git, working merges. Today was the rehearsal. Tomorrow we slow down and learn
to *write* the Spring the AI was writing for you — so you can tell when it's lying."

---

## Assessment lens (informal today)

Not graded hard, but call these out so they internalise the priorities:
1. **Working merges on `main`** (does a fresh clone run?) — the headline number.
2. **Git health** — PRs, reviews, a resolved conflict, no long-lived branches, green `main`.
3. **Verification discipline** — did they catch the AI's misses, or merge them?
4. **Coordination** — did the team split work sanely, or trip over one file all afternoon?

Order matters: a team of 4 clean merges + a resolved conflict + caught AI bugs beats a team
of 8 features and a red `main`. Say so out loud at the start *and* the end.
