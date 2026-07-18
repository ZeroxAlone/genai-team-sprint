# AGENTS.md — how an AI assistant should help on this repo

*Read by Copilot / Claude / Cursor when working in this project. Humans on the team: the
rules you're held to are in `../constitution.md` and `../requirements.md`.*

## What this repo is
A full-stack FX app **skeleton** that already runs (`docker compose up`): MySQL (seeded),
Spring Boot backend, static HTML/JS front end. A team is racing an ordered backlog of
features onto it, one branch → PR → merge each, during a ~3-hour sprint.

## Your job
Help the team **ship working features fast, and understand what you produced.** Speed is the
point today — but a feature nobody on the team can explain is a liability, not a win.

## Hard rules (do not break)
1. **Obey the constitution.** Stack is fixed: Spring Boot 3.3 / Java 21 / MySQL / plain
   HTML-CSS-JS served from `src/main/resources/static`. No frontend framework, no build step,
   no new heavy dependencies without being asked.
2. **Build ONLY what the current feature asks for.** Respect its "out of scope" list. Do not
   add auth, pagination, caching, config screens, or extra endpoints nobody requested.
3. **Copy the sample slice as the pattern.** `com.fx.sample` (Currency model → repository →
   controller) + `static/currencies.html` + `static/js/currencies.js` is the reference shape
   for every new feature. Match its style.
4. **Use the pinned checkpoints — never invent numbers.** EUR/USD on 2026-01-12 = 1.0818;
   the FeeCalculator tiers are in the constitution. If a value matters and isn't given, ask.
5. **No stack traces to the browser.** Errors return clean JSON via `com.fx.web.ApiExceptionHandler`.
6. **Explain as you go.** For each change, say in one line what it does and why. If asked
   "why", justify it. Prefer several small steps the team can follow over one big dump.

## How to work a feature
1. Read the feature's acceptance criteria. Restate them as a short plan (files, endpoint,
   query, UI change). Wait for a "go".
2. Implement the smallest thing that satisfies the criteria, following the sample slice.
3. Tell the human exactly how to run and verify **each** acceptance criterion.
4. If a criterion fails, help them decide: was the spec ambiguous (fix the spec, regenerate)
   or a real bug (fix it, add the case as a test)? Prefer fixing the spec.

## The database (already seeded — read it, don't recreate it)
Schema `fxdb`, tables: `currency`, `account`, `fx_rate` (holds history — "latest" = max
`rate_date` per pair), `transfer`. Access via `JdbcTemplate` like `CurrencyRepository` does.
Don't add JPA/Hibernate; don't change the schema unless a feature explicitly needs a column.

## Testing
Web-slice tests like `CurrencyControllerTest` (mock the repository, no DB) run in CI. Add one
per controller you create — it keeps `main` honest and CI green.
