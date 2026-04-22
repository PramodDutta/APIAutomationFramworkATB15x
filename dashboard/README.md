# ATB API Test Dashboard

A lightweight React + SQLite dashboard that ingests TestNG Surefire results from this Maven project and visualizes pass/fail counts, per-run detail, daily trends, and top failing tests. Login-protected, multi-user, auto-refreshes.

```
dashboard/
├── backend/      Express + better-sqlite3 + JWT auth  (port 4000)
├── frontend/     Vite + React + Recharts               (port 5180)
└── ingest/       Parses target/surefire-reports/testng-results.xml
```

## One-time setup

```bash
cd dashboard/backend  && npm install
cd dashboard/frontend && npm install
cd dashboard/ingest   && npm install
```

Start the backend once (creates SQLite DB + seeds default admin):

```bash
cd dashboard/backend && npm start
# → [db] Seeded default user admin / admin123
# → [dashboard] backend listening on http://localhost:4000
```

In another terminal, start the UI:

```bash
cd dashboard/frontend && npm run dev
# → http://localhost:5180
```

Default login: **admin / admin123** (change it under Settings → Your account).

## Feeding test runs into the dashboard

Every Maven run produces `target/surefire-reports/testng-results.xml`. Parse it into SQLite with:

```bash
mvn test -Dsurefire.suiteXmlFiles=testng-e2e.xml
node dashboard/ingest/ingest.js
```

Each `node dashboard/ingest/ingest.js` call inserts **one new run** (with all per-test rows, durations, error messages, git branch/commit). The dashboard auto-refreshes every 15 s.

### Automating ingestion

Append the ingest command to any pipeline step that follows `mvn test`. For Jenkins, add a post-test stage:

```groovy
stage('Ingest to Dashboard') {
    steps {
        sh 'node dashboard/ingest/ingest.js || true'
    }
}
```

`|| true` keeps pipeline status independent of dashboard availability.

## Views

| Route | Purpose |
|---|---|
| `/` Overview | Latest-run tiles, pass-rate donut, top failing tests bar chart |
| `/runs` | Paginated history of all runs with quick status columns |
| `/runs/:id` | Per-test-case detail for one run (status, duration, error) |
| `/trends` | Daily pass-rate line + stacked pass/fail/skip bars, 7–90 day range |
| `/settings` | Change password, list/add users, ingestion instructions |

## Data model (SQLite)

```sql
users        (id, username, password_hash, role, created_at)
test_runs    (id, suite_name, started_at, finished_at, duration_ms,
              total, passed, failed, skipped, ignored,
              env, git_branch, git_commit, ingested_at)
test_cases   (id, run_id → test_runs.id, class_name, test_name,
              status, duration_ms, started_at, error_message, is_config)
```

DB file lives at `dashboard/backend/data/dashboard.sqlite` (gitignored). Delete it to reset.

## Security notes

- JWT secret defaults to a dev value. Set `JWT_SECRET` env var in production.
- Passwords are bcrypt-hashed. The seeded admin password (`admin123`) should be changed on first login.
- The ingest script writes directly to SQLite (no HTTP), so it works even when the backend is offline.
