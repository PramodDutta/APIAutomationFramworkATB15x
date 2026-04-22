import React, { useEffect, useState } from 'react';
import { api } from '../api.js';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
  BarChart, Bar } from 'recharts';

export default function Trends() {
  const [days, setDays] = useState(30);
  const [data, setData] = useState([]);
  const [err, setErr] = useState('');

  const load = async () => {
    try { setData(await api(`/api/stats/daily?days=${days}`)); }
    catch (e) { setErr(e.message); }
  };

  useEffect(() => { load(); }, [days]);

  const withRate = data.map((d) => ({
    ...d,
    passRate: d.total > 0 ? +((d.passed / d.total) * 100).toFixed(1) : 0,
  }));

  if (err) return <div className="error">Error: {err}</div>;

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Trends</h1>
          <p className="muted">Daily test health over the last {days} day{days > 1 ? 's' : ''}</p>
        </div>
        <div className="filters">
          {[7, 14, 30, 60, 90].map((n) => (
            <button key={n} className={'btn-ghost' + (days === n ? ' active' : '')} onClick={() => setDays(n)}>
              {n}d
            </button>
          ))}
        </div>
      </header>

      {withRate.length === 0 ? (
        <div className="card muted">No data yet for this range. Run the tests and ingest results.</div>
      ) : (
        <>
          <section className="card">
            <h3>Pass rate (%)</h3>
            <div style={{ height: 280 }}>
              <ResponsiveContainer>
                <LineChart data={withRate}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="day" />
                  <YAxis domain={[0, 100]} />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="passRate" name="Pass %" stroke="#10b981" strokeWidth={2} dot />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </section>

          <section className="card">
            <h3>Daily counts</h3>
            <div style={{ height: 280 }}>
              <ResponsiveContainer>
                <BarChart data={withRate}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="day" />
                  <YAxis allowDecimals={false} />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="passed" name="Passed" stackId="a" fill="#10b981" />
                  <Bar dataKey="failed" name="Failed" stackId="a" fill="#ef4444" />
                  <Bar dataKey="skipped" name="Skipped" stackId="a" fill="#f59e0b" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </section>

          <section className="card">
            <h3>Runs per day</h3>
            <div style={{ height: 220 }}>
              <ResponsiveContainer>
                <BarChart data={withRate}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="day" />
                  <YAxis allowDecimals={false} />
                  <Tooltip />
                  <Bar dataKey="runs" name="Runs" fill="#6366f1" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </section>
        </>
      )}
    </div>
  );
}
