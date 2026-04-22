#!/usr/bin/env node
/**
 * Parses target/surefire-reports/testng-results.xml and inserts a test run
 * (plus all test-case rows) into the dashboard SQLite DB.
 *
 * Usage:
 *   node dashboard/ingest/ingest.js                # uses default paths
 *   node dashboard/ingest/ingest.js path/to/testng-results.xml
 *
 * Safe to run even if the backend server isn't up — it writes directly to SQLite.
 */
import fs from 'node:fs';
import path from 'node:path';
import { execSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import Database from 'better-sqlite3';
import { XMLParser } from 'fast-xml-parser';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const REPO_ROOT = path.resolve(__dirname, '..', '..');
const DEFAULT_XML = path.join(REPO_ROOT, 'target', 'surefire-reports', 'testng-results.xml');
const DB_PATH = path.join(__dirname, '..', 'backend', 'data', 'dashboard.sqlite');

const xmlPath = process.argv[2] || DEFAULT_XML;
if (!fs.existsSync(xmlPath)) {
  console.error(`[ingest] results XML not found: ${xmlPath}`);
  console.error('[ingest] run `mvn test` first, or pass an XML path.');
  process.exit(1);
}
if (!fs.existsSync(DB_PATH)) {
  console.error(`[ingest] DB not found at ${DB_PATH}. Start the backend once (npm start in dashboard/backend) to initialize it.`);
  process.exit(1);
}

const xml = fs.readFileSync(xmlPath, 'utf8');
const parser = new XMLParser({
  ignoreAttributes: false,
  attributeNamePrefix: '',
  allowBooleanAttributes: true,
  parseAttributeValue: false,
  textNodeName: '#text',
});
const parsed = parser.parse(xml);
const results = parsed['testng-results'];
if (!results) {
  console.error('[ingest] unexpected XML structure — missing <testng-results>');
  process.exit(1);
}

// TestNG attributes come back as strings; normalize to ints.
const num = (v) => (v == null ? 0 : parseInt(v, 10) || 0);
const totals = {
  total: num(results.total),
  passed: num(results.passed),
  failed: num(results.failed),
  skipped: num(results.skipped),
  ignored: num(results.ignored),
};

// Handle one-or-many <suite>, <test>, <class>, <test-method>.
const arr = (x) => (x == null ? [] : Array.isArray(x) ? x : [x]);

function isoFromTestng(s) {
  if (!s) return null;
  // "2026-04-20T08:24:33 IST" → "2026-04-20T08:24:33" (ISO-like; keep local timestamp)
  return s.replace(/\s+[A-Z]{2,5}$/, '');
}

const suites = arr(results.suite);
const suite = suites[0] || {};
const suiteName = suite.name || 'Unknown';
const startedAt = isoFromTestng(suite['started-at']) || new Date().toISOString().slice(0, 19);
const finishedAt = isoFromTestng(suite['finished-at']);
const durationMs = num(suite['duration-ms']);

const cases = [];
for (const s of suites) {
  for (const t of arr(s.test)) {
    for (const c of arr(t.class)) {
      const className = c.name;
      for (const m of arr(c['test-method'])) {
        const errMsg = (() => {
          const ex = m.exception;
          if (!ex) return null;
          const line = ex.message || (ex['full-stacktrace'] || '').toString().split('\n')[0];
          return (line || '').toString().trim().slice(0, 2000) || null;
        })();
        cases.push({
          class_name: className,
          test_name: m.name,
          status: (m.status || '').toUpperCase(),
          duration_ms: num(m['duration-ms']),
          started_at: isoFromTestng(m['started-at']),
          error_message: errMsg,
          is_config: m['is-config'] === 'true' || m['is-config'] === true ? 1 : 0,
        });
      }
    }
  }
}

function gitInfo() {
  try {
    const branch = execSync('git rev-parse --abbrev-ref HEAD', { cwd: REPO_ROOT }).toString().trim();
    const commit = execSync('git rev-parse --short HEAD', { cwd: REPO_ROOT }).toString().trim();
    return { branch, commit };
  } catch {
    return { branch: null, commit: null };
  }
}
const git = gitInfo();

const db = new Database(DB_PATH);
db.pragma('foreign_keys = ON');

const insertRun = db.prepare(`INSERT INTO test_runs
  (suite_name, started_at, finished_at, duration_ms, total, passed, failed, skipped, ignored, env, git_branch, git_commit)
  VALUES (@suite_name, @started_at, @finished_at, @duration_ms, @total, @passed, @failed, @skipped, @ignored, @env, @git_branch, @git_commit)`);
const insertCase = db.prepare(`INSERT INTO test_cases
  (run_id, class_name, test_name, status, duration_ms, started_at, error_message, is_config)
  VALUES (?, ?, ?, ?, ?, ?, ?, ?)`);

const tx = db.transaction(() => {
  const info = insertRun.run({
    suite_name: suiteName,
    started_at: startedAt,
    finished_at: finishedAt,
    duration_ms: durationMs,
    total: totals.total,
    passed: totals.passed,
    failed: totals.failed,
    skipped: totals.skipped,
    ignored: totals.ignored,
    env: process.env.TEST_ENV || 'local',
    git_branch: git.branch,
    git_commit: git.commit,
  });
  const runId = info.lastInsertRowid;
  for (const c of cases) {
    insertCase.run(runId, c.class_name, c.test_name, c.status, c.duration_ms, c.started_at, c.error_message, c.is_config);
  }
  return runId;
});

const runId = tx();
console.log(`[ingest] stored run #${runId}: ${suiteName} — ${totals.passed}/${totals.total} passed, ${totals.failed} failed, ${totals.skipped} skipped (${cases.length} cases)`);
