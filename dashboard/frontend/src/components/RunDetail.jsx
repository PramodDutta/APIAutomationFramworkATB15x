import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from '../api.js';

export default function RunDetail() {
  const { id } = useParams();
  const [run, setRun] = useState(null);
  const [err, setErr] = useState('');

  useEffect(() => {
    api(`/api/runs/${id}`).then(setRun).catch((e) => setErr(e.message));
  }, [id]);

  if (err) return <div className="error">Error: {err}</div>;
  if (!run) return <div className="muted">Loading…</div>;

  const cases = run.cases.filter((c) => !c.is_config);
  const configs = run.cases.filter((c) => c.is_config);

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Run #{run.id}</h1>
          <p className="muted">
            {run.suite_name} · started {new Date(run.started_at).toLocaleString()} · {(run.duration_ms / 1000).toFixed(2)}s
          </p>
        </div>
        <Link className="btn" to="/runs">← Back</Link>
      </header>

      <section className="tiles small">
        <div className="tile"><div className="tile-label">Total</div><div className="tile-value">{run.total}</div></div>
        <div className="tile tone-good"><div className="tile-label">Passed</div><div className="tile-value">{run.passed}</div></div>
        <div className="tile tone-bad"><div className="tile-label">Failed</div><div className="tile-value">{run.failed}</div></div>
        <div className="tile tone-warn"><div className="tile-label">Skipped</div><div className="tile-value">{run.skipped}</div></div>
      </section>

      <div className="card no-pad">
        <table className="table">
          <thead>
            <tr><th>Class</th><th>Test</th><th>Status</th><th>Duration</th><th>Error</th></tr>
          </thead>
          <tbody>
            {cases.map((c) => (
              <tr key={c.id}>
                <td className="mono">{c.class_name.split('.').pop()}</td>
                <td>{c.test_name}</td>
                <td><span className={`pill pill-${c.status.toLowerCase()}`}>{c.status}</span></td>
                <td>{(c.duration_ms / 1000).toFixed(2)}s</td>
                <td className="err-cell">{c.error_message || ''}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {configs.length > 0 && (
        <details className="card" style={{ marginTop: 16 }}>
          <summary>Setup / teardown methods ({configs.length})</summary>
          <table className="table">
            <thead><tr><th>Class</th><th>Method</th><th>Status</th><th>Duration</th></tr></thead>
            <tbody>
              {configs.map((c) => (
                <tr key={c.id}>
                  <td className="mono">{c.class_name.split('.').pop()}</td>
                  <td>{c.test_name}</td>
                  <td><span className={`pill pill-${c.status.toLowerCase()}`}>{c.status}</span></td>
                  <td>{(c.duration_ms / 1000).toFixed(2)}s</td>
                </tr>
              ))}
            </tbody>
          </table>
        </details>
      )}
    </div>
  );
}
