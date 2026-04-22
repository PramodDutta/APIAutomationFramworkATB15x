import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api.js';

export default function Runs() {
  const [runs, setRuns] = useState([]);
  const [err, setErr] = useState('');

  const load = async () => {
    try { setRuns(await api('/api/runs?limit=100')); }
    catch (e) { setErr(e.message); }
  };

  useEffect(() => { load(); }, []);

  if (err) return <div className="error">Error: {err}</div>;

  return (
    <div className="page">
      <header className="page-header">
        <h1>Runs</h1>
        <button className="btn" onClick={load}>Refresh</button>
      </header>
      <div className="card no-pad">
        <table className="table">
          <thead>
            <tr>
              <th>#</th><th>When</th><th>Suite</th><th>Total</th><th>Pass</th><th>Fail</th><th>Skip</th><th>Duration</th><th>Branch</th><th></th>
            </tr>
          </thead>
          <tbody>
            {runs.length === 0 && (
              <tr><td colSpan="10" className="muted center">No runs yet.</td></tr>
            )}
            {runs.map((r) => (
              <tr key={r.id}>
                <td>{r.id}</td>
                <td>{new Date(r.started_at).toLocaleString()}</td>
                <td>{r.suite_name}</td>
                <td>{r.total}</td>
                <td className="good">{r.passed}</td>
                <td className={r.failed > 0 ? 'bad' : ''}>{r.failed}</td>
                <td className={r.skipped > 0 ? 'warn' : ''}>{r.skipped}</td>
                <td>{(r.duration_ms / 1000).toFixed(2)}s</td>
                <td>{r.git_branch || '—'}</td>
                <td><Link to={`/runs/${r.id}`}>View</Link></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
