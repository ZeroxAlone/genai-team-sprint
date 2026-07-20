# Team Git Playbook — *how a team ships in parallel without breaking main*

*w2d1-a. This is the real skill under test this afternoon. The FX features are the excuse;
**coordinating four people through one `main` branch** is the lesson. Get this scaffolding
up in the first 20 minutes, then race the requirements.*

---

## Part 1 — Stand the team repo up (first 20 min, do it together)

One person is **repo owner**; the rest are **collaborators**.

**Owner:**
1. Take the skeleton. On GitHub, create a repo `fx-app-<teamname>` and push the skeleton to
   it (the skeleton README has the exact `git remote add` / `git push -u origin main` lines).
2. **Settings → Collaborators** → add every teammate by GitHub username. They accept the
   invite (check email / the repo's notifications). **Do this first** — invites can lag.
3. **Settings → Branches → Add rule** for `main`:
   - ✅ Require a pull request before merging
   - ✅ Require **1 approval**
   - (leave status checks off — no CI today)
   This is what makes "no direct pushes to main" real instead of a promise.

**Everyone:**
4. `git clone` the team repo. Confirm `docker compose up` boots and `/health` is green
   **before you touch anything** — if the skeleton doesn't run for you, fix that now
   (JAVA_HOME/Docker), not at 4pm mid-feature.
5. Agree a **board**: paste `requirements.md`'s scoreboard into the repo's Issues or a shared
   doc; each person claims requirements by putting their name on rows. **Claim before you
   branch** — that's how you avoid two people building 001.

> ⚠ **Windows/JAVA_HOME reminder.** If Maven picks the wrong JDK, set `JAVA_HOME` for the
> session before `docker compose`/`mvnw` (your Day-0 note covers it). Sort this in step 4.

---

## Part 2 — The per-feature loop (repeat all afternoon)

```
git switch main && git pull            # 1. start from the freshest main, ALWAYS
git switch -c feat/003-convert         # 2. one branch per requirement (name it by #)
   ...build the feature (Copilot + hand-code)...
docker compose up  → tick every AC     # 3. verify YOURSELF first
git add -A && git commit -m "feat(003): conversion calculator + fee"
git push -u origin feat/003-convert    # 4. push the branch
   open a PR → describe the ACs → request a teammate review
   teammate runs it, ticks the ACs, approves
git switch main && git pull            # 5. after merge, everyone pulls main again
```

Rules that keep `main` green:
- **Small branches, merged often.** A branch that lives 20 minutes rarely conflicts. A branch
  that lives two hours is a merge nightmare. Ship 001, pull, ship 002 — don't hoard.
- **Pull `main` before you branch AND before you open the PR.** The second pull is where you
  catch conflicts on *your* machine instead of dumping them on the reviewer.
- **You verify before you ask for review.** The reviewer confirms; they shouldn't be the one
  discovering it doesn't run.
- **Only the reviewer merges, and only after the ACs pass.** Green `main` is a team asset.

---

## Part 3 — The conflict is coming (embrace it)

Look at `requirements.md`'s *Touches* column. Several requirements edit the **home page**
(001, 002, 008) and the **convert page** (003, 006). If two people build those at once, git
**will** hand you a merge conflict. That's not a failure — **it's the exercise**. Two ways
to handle it, both worth practising once today:

**Avoid it (coordination):** whoever owns the home page merges 001 first; 002/008 start
*after* 001 is on `main` and they `git pull`. Talk before you branch.

**Resolve it (when it happens anyway):**
```
git switch feat/002-lookup
git pull origin main              # main moved under you (001 merged)
# CONFLICT in home.html / index.js
```
Open the file, find the `<<<<<<< ======= >>>>>>>` markers, keep **both** features' changes
(you want the table *and* the lookup box), delete the markers, then:
```
docker compose up   # prove BOTH features still work after the merge
git add -A && git commit
git push
```
Do this **as a pair, out loud** the first time — it's the single most valuable five minutes
of the afternoon. A conflict resolved well is a better learning outcome than a clean feature.

---

## Part 4 — Coordination patterns that work

- **Split by page, not by layer.** "You own the convert page end-to-end (endpoint + UI),
  I own history end-to-end" conflicts far less than "you do all backends, I do all
  frontends" — the latter has you both in the router constantly.
- **Land the foundation first, together.** 001 unblocks almost everything and touches the
  shared home page. Do it as a mob in the first half hour, merge it, *then* fan out.
- **A 30-second stand-up every ~40 min.** "I just merged 003, the convert page is on main,
  pull before you start 006." Cheap, saves hours.
- **`main` breaks? Stop the line.** A broken `main` blocks everyone's next `pull`. Revert the
  offending merge (`git revert`) first, fix on a branch second. Don't leave teammates
  branching off a broken base.

---

## What "good" looks like at 4:30

Not the most features — the **healthiest history**:
- `git log --oneline main` reads as a clean sequence of merged features, each one working.
- Every merge came through a PR with an approval.
- At least one merge conflict was resolved (and both features still worked after).
- `docker compose up` on a fresh clone of `main` boots and every merged feature demos.

A team that merged **four working features through clean PRs** has won this afternoon over a
team that wrote eight and left `main` red. **Working-and-merged is the only currency.**
