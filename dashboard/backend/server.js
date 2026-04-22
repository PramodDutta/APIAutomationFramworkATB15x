import express from 'express';
import cors from 'cors';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { db } from './db.js';

const app = express();
const PORT = process.env.PORT || 4000;
const JWT_SECRET = process.env.JWT_SECRET || 'atb-dashboard-dev-secret-change-me';

app.use(cors());
app.use(express.json({ limit: '10mb' }));

function auth(req, res, next) {
  const header = req.headers.authorization || '';
  const token = header.startsWith('Bearer ') ? header.slice(7) : null;
  if (!token) return res.status(401).json({ error: 'missing token' });
  try {
    req.user = jwt.verify(token, JWT_SECRET);
    next();
  } catch {
    return res.status(401).json({ error: 'invalid token' });
  }
}

app.get('/api/health', (_req, res) => res.json({ ok: true }));

app.post('/api/auth/login', (req, res) => {
  const { username, password } = req.body || {};
  if (!username || !password) return res.status(400).json({ error: 'username and password required' });
  const user = db.prepare('SELECT * FROM users WHERE username = ?').get(username);
  if (!user || !bcrypt.compareSync(password, user.password_hash)) {
    return res.status(401).json({ error: 'invalid credentials' });
  }
  const token = jwt.sign({ sub: user.id, username: user.username, role: user.role }, JWT_SECRET, { expiresIn: '12h' });
  res.json({ token, user: { id: user.id, username: user.username, role: user.role } });
});

app.post('/api/auth/change-password', auth, (req, res) => {
  const { currentPassword, newPassword } = req.body || {};
  if (!currentPassword || !newPassword) return res.status(400).json({ error: 'fields required' });
  const user = db.prepare('SELECT * FROM users WHERE id = ?').get(req.user.sub);
  if (!user || !bcrypt.compareSync(currentPassword, user.password_hash)) {
    return res.status(401).json({ error: 'current password incorrect' });
  }
  const hash = bcrypt.hashSync(newPassword, 10);
  db.prepare('UPDATE users SET password_hash = ? WHERE id = ?').run(hash, user.id);
  res.json({ ok: true });
});

app.get('/api/users', auth, (_req, res) => {
  const rows = db.prepare('SELECT id, username, role, created_at FROM users ORDER BY id').all();
  res.json(rows);
});

app.post('/api/users', auth, (req, res) => {
  const { username, password, role } = req.body || {};
  if (!username || !password) return res.status(400).json({ error: 'fields required' });
  const exists = db.prepare('SELECT 1 FROM users WHERE username = ?').get(username);
  if (exists) return res.status(409).json({ error: 'username exists' });
  const hash = bcrypt.hashSync(password, 10);
  const info = db.prepare('INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)')
    .run(username, hash, role || 'user');
  res.json({ id: info.lastInsertRowid, username, role: role || 'user' });
});

app.post('/api/runs', (req, res) => {
  const r = req.body || {};
  if (!r.started_at) return res.status(400).json({ error: 'started_at required' });
  const runInsert = db.prepare(`INSERT INTO test_runs
    (suite_name, started_at, finished_at, duration_ms, total, passed, failed, skipped, ignored, env, git_branch, git_commit)
    VALUES (@suite_name, @started_at, @finished_at, @duration_ms, @total, @passed, @failed, @skipped, @ignored, @env, @git_branch, @git_commit)`);
  const caseInsert = db.prepare(`INSERT INTO test_cases
    (run_id, class_name, test_name, status, duration_ms, started_at, error_message, is_config)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)`);
  const tx = db.transaction(() => {
    const runInfo = runInsert.run({
      suite_name: r.suite_name || null,
      started_at: r.started_at,
      finished_at: r.finished_at || null,
      duration_ms: r.duration_ms || 0,
      total: r.total || 0,
      passed: r.passed || 0,
      failed: r.failed || 0,
      skipped: r.skipped || 0,
      ignored: r.ignored || 0,
      env: r.env || null,
      git_branch: r.git_branch || null,
      git_commit: r.git_commit || null,
    });
    const runId = runInfo.lastInsertRowid;
    for (const tc of r.cases || []) {
      caseInsert.run(runId, tc.class_name, tc.test_name, tc.status,
        tc.duration_ms || 0, tc.started_at || null, tc.error_message || null, tc.is_config ? 1 : 0);
    }
    return runId;
  });
  const id = tx();
  res.json({ id });
});

app.get('/api/runs', auth, (req, res) => {
  const limit = Math.min(parseInt(req.query.limit) || 50, 500);
  const rows = db.prepare('SELECT * FROM test_runs ORDER BY started_at DESC LIMIT ?').all(limit);
  res.json(rows);
});

app.get('/api/runs/:id', auth, (req, res) => {
  const run = db.prepare('SELECT * FROM test_runs WHERE id = ?').get(req.params.id);
  if (!run) return res.status(404).json({ error: 'not found' });
  const cases = db.prepare('SELECT * FROM test_cases WHERE run_id = ? ORDER BY id').all(run.id);
  res.json({ ...run, cases });
});

app.delete('/api/runs/:id', auth, (req, res) => {
  db.prepare('DELETE FROM test_runs WHERE id = ?').run(req.params.id);
  res.json({ ok: true });
});

app.get('/api/stats/overview', auth, (_req, res) => {
  const latest = db.prepare('SELECT * FROM test_runs ORDER BY started_at DESC LIMIT 1').get();
  const totals = db.prepare(`SELECT
      COUNT(*) AS runs,
      COALESCE(SUM(total),0) AS total,
      COALESCE(SUM(passed),0) AS passed,
      COALESCE(SUM(failed),0) AS failed,
      COALESCE(SUM(skipped),0) AS skipped
    FROM test_runs`).get();
  const last7 = db.prepare(`SELECT COUNT(*) AS c FROM test_runs
      WHERE started_at >= datetime('now', '-7 days')`).get().c;
  res.json({ latest, totals, runsLast7Days: last7 });
});

app.get('/api/stats/daily', auth, (req, res) => {
  const days = Math.min(parseInt(req.query.days) || 30, 180);
  const rows = db.prepare(`SELECT
      substr(started_at, 1, 10) AS day,
      COUNT(*) AS runs,
      SUM(total) AS total,
      SUM(passed) AS passed,
      SUM(failed) AS failed,
      SUM(skipped) AS skipped
    FROM test_runs
    WHERE started_at >= datetime('now', ?)
    GROUP BY day
    ORDER BY day ASC`).all(`-${days} days`);
  res.json(rows);
});

app.get('/api/stats/top-failing', auth, (_req, res) => {
  const rows = db.prepare(`SELECT class_name, test_name,
      COUNT(*) AS failures,
      MAX(started_at) AS last_failure
    FROM test_cases
    WHERE status = 'FAIL' AND is_config = 0
    GROUP BY class_name, test_name
    ORDER BY failures DESC, last_failure DESC
    LIMIT 10`).all();
  res.json(rows);
});

app.listen(PORT, () => {
  console.log(`[dashboard] backend listening on http://localhost:${PORT}`);
});
