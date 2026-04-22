import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../AuthContext.jsx';

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const doLogout = () => {
    logout();
    navigate('/login');
  };

  const linkCls = ({ isActive }) => 'nav-link' + (isActive ? ' active' : '');

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">ATB</div>
          <div>
            <div className="brand-title">API Test Dashboard</div>
            <div className="brand-sub">Restful Booker suite</div>
          </div>
        </div>
        <nav className="nav">
          <div className="nav-section">Dashboard</div>
          <NavLink to="/" end className={linkCls}>Overview</NavLink>
          <NavLink to="/runs" className={linkCls}>Runs</NavLink>
          <NavLink to="/trends" className={linkCls}>Trends</NavLink>
          <div className="nav-section">Settings</div>
          <NavLink to="/settings" className={linkCls}>Users &amp; Preferences</NavLink>
        </nav>
        <div className="sidebar-footer">
          <div className="user-chip">
            <div className="avatar">{(user?.username || '?').slice(0, 1).toUpperCase()}</div>
            <div>
              <div className="u-name">{user?.username}</div>
              <div className="u-role">{user?.role}</div>
            </div>
          </div>
          <button className="btn-ghost" onClick={doLogout}>Sign out</button>
        </div>
      </aside>
      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}
