import React, { useEffect, useState } from 'react';
import { api } from '../api.js';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend,
  BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts';
import { Link } from 'react-router-dom';

const COLORS = { PASS: '#10b981', FAIL: '#ef4444', SKIP: '#f59e0b' };

function Tile({ label, value, tone }) {
  return (
    <div className={`tile tone-${tone || 'default'}`}>
      <div className="tile-label">{label}</div>
      <div className="tile-value">{value}</div>
    </div>
  );
}

export default function Overview() {
  const [data, setData] = useState(null);
  const [topFailing, setTopFailing] = useState([]);
  const [err, setErr] = useState('');

  const load = async () => {
    try {
      const [overview, failing] = await Promise.all([
        api('/api/stats/overview'),
        api('/api/stats/top-failing'),
      ]);
      setData(overview);
      setTopFailing(failing);
    } catch (e) { setErr(e.message); }
  };

  useEffect(() => {
    load();
    const t = setInterval(load, 15000);
    return () => clearInterval(t);
  }, []);

  if (err) return <div className="error">Error: {err}</div>;
  if (!data) return <div className="muted">Loading…</div>;

  const latest = data.latest;
  const pieData = latest ? [
    { name: 'Passed', value: latest.passed, color: COLORS.PASS },
    { name: 'Failed', value: latest.failed, color: COLORS.FAIL },
    { name: 'Skipped', value: latest.skipped, color: COLORS.SKIP },
  ].filter((x) => x.value > 0) : [];

  const passRate = latest && latest.total > 0 ? Math.round((latest.passed / latest.total) * 1000) / 10 : 0;

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Overview</h1>
          <p className="muted">
            {latest
              ? <>Latest run: <strong>{latest.suite_name}</strong> · {new Date(latest.started_at).toLocaleString()}</>
              : 'No runs yet — run `mvn test` then `node dashboard/ingest/ingest.js`.'}
          </p>
        </div>
        <button className="btn" onClick={load}>Refresh</button>
      </header>

      <section className="tiles">
        <Tile label="Total runs" value={data.totals.runs} />
        <Tile label="Runs (last 7d)" value={data.runsLast7Days} />
        <Tile label="Latest pass rate" value={`${passRate}%`} tone={passRate === 100 ? 'good' : passRate >= 80 ? 'warn' : 'bad'} />
        <Tile label="All-time passed" value={data.totals.passed} tone="good" />
        <Tile label="All-time failed" value={data.totals.failed} tone={data.totals.failed > 0 ? 'bad' : 'default'} />
        <Tile label="All-time skipped" value={data.totals.skipped} tone="warn" />
      </section>

      {latest && (
        <section className="grid-2">
          <div className="card">
            <h3>Latest run breakdown</h3>
            <div style={{ height: 260 }}>
              <ResponsiveContainer>
                <PieChart>
                  <Pie data={pieData} dataKey="value" nameKey="name" innerRadius={55} outerRadius={95} paddingAngle={2}>
                    {pieData.map((entry, idx) => <Cell key={idx} fill={entry.color} />)}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div className="run-meta">
              <div><span className="muted">Total:</span> {latest.total}</div>
              <div><span className="muted">Duration:</span> {(latest.duration_ms / 1000).toFixed(2)}s</div>
              <div><span className="muted">Branch:</span> {latest.git_branch || '—'}</div>
              <div><span className="muted">Commit:</span> <code>{latest.git_commit || '—'}</code></div>
              <div><Link to={`/runs/${latest.id}`}>View run details →</Link></div>
            </div>
          </div>

          <div className="card">
            <h3>Top failing tests (all-time)</h3>
            {topFailing.length === 0 ? (
              <div className="muted">No failures recorded. 🎉</div>
            ) : (
              <div style={{ height: 260 }}>
                <ResponsiveContainer>
                  <BarChart data={topFailing.map((r) => ({ name: r.test_name, failures: r.failures }))} layout="vertical" margin={{ left: 80 }}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis type="number" allowDecimals={false} />
                    <YAxis type="category" dataKey="name" width={160} />
                    <Tooltip />
                    <Bar dataKey="failures" fill={COLORS.FAIL} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            )}
          </div>
        </section>
      )}
    </div>
  );
}
