import React, { useEffect, useState } from 'react';
import { api } from '../api.js';
import { useAuth } from '../AuthContext.jsx';

export default function Settings() {
  const { user } = useAuth();
  const [users, setUsers] = useState([]);
  const [err, setErr] = useState('');
  const [msg, setMsg] = useState('');
  const [newU, setNewU] = useState({ username: '', password: '', role: 'user' });
  const [pw, setPw] = useState({ currentPassword: '', newPassword: '' });

  const load = async () => {
    try { setUsers(await api('/api/users')); } catch (e) { setErr(e.message); }
  };
  useEffect(() => { load(); }, []);

  const createUser = async (e) => {
    e.preventDefault();
    setErr(''); setMsg('');
    try {
      await api('/api/users', { method: 'POST', body: JSON.stringify(newU) });
      setMsg(`User "${newU.username}" created.`);
      setNewU({ username: '', password: '', role: 'user' });
      load();
    } catch (e) { setErr(e.message); }
  };

  const changePw = async (e) => {
    e.preventDefault();
    setErr(''); setMsg('');
    try {
      await api('/api/auth/change-password', { method: 'POST', body: JSON.stringify(pw) });
      setMsg('Password updated.');
      setPw({ currentPassword: '', newPassword: '' });
    } catch (e) { setErr(e.message); }
  };

  return (
    <div className="page">
      <header className="page-header">
        <h1>Settings</h1>
      </header>

      {err && <div className="error">{err}</div>}
      {msg && <div className="notice">{msg}</div>}

      <section className="card">
        <h3>Ingestion</h3>
        <p className="muted">Dashboard data is fed from Maven Surefire output. To refresh after a test run:</p>
        <pre className="code">
mvn test -Dsurefire.suiteXmlFiles=testng-e2e.xml
node dashboard/ingest/ingest.js
        </pre>
        <p className="muted">The ingest script parses <code>target/surefire-reports/testng-results.xml</code> and writes a new run record. Overview and Trends pages auto-refresh every 15 seconds.</p>
      </section>

      <section className="card">
        <h3>Your account</h3>
        <p className="muted">Signed in as <strong>{user?.username}</strong> ({user?.role})</p>
        <form onSubmit={changePw} className="form-grid">
          <label>Current password</label>
          <input type="password" value={pw.currentPassword} onChange={(e) => setPw({ ...pw, currentPassword: e.target.value })} required />
          <label>New password</label>
          <input type="password" value={pw.newPassword} onChange={(e) => setPw({ ...pw, newPassword: e.target.value })} required />
          <div />
          <button type="submit" className="btn">Change password</button>
        </form>
      </section>

      <section className="card">
        <h3>Users</h3>
        <table className="table">
          <thead><tr><th>ID</th><th>Username</th><th>Role</th><th>Created</th></tr></thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td><td>{u.username}</td><td>{u.role}</td><td>{new Date(u.created_at).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <h4 style={{ marginTop: 20 }}>Add user</h4>
        <form onSubmit={createUser} className="form-grid">
          <label>Username</label>
          <input value={newU.username} onChange={(e) => setNewU({ ...newU, username: e.target.value })} required />
          <label>Password</label>
          <input type="password" value={newU.password} onChange={(e) => setNewU({ ...newU, password: e.target.value })} required />
          <label>Role</label>
          <select value={newU.role} onChange={(e) => setNewU({ ...newU, role: e.target.value })}>
            <option value="user">user</option>
            <option value="admin">admin</option>
          </select>
          <div />
          <button type="submit" className="btn">Create user</button>
        </form>
      </section>
    </div>
  );
}
