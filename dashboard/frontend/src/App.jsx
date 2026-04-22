import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout.jsx';
import Login from './components/Login.jsx';
import Overview from './components/Overview.jsx';
import Runs from './components/Runs.jsx';
import RunDetail from './components/RunDetail.jsx';
import Trends from './components/Trends.jsx';
import Settings from './components/Settings.jsx';
import { useAuth } from './AuthContext.jsx';

function Protected({ children }) {
  const { isAuthed } = useAuth();
  return isAuthed() ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <Protected>
            <Layout />
          </Protected>
        }
      >
        <Route index element={<Overview />} />
        <Route path="runs" element={<Runs />} />
        <Route path="runs/:id" element={<RunDetail />} />
        <Route path="trends" element={<Trends />} />
        <Route path="settings" element={<Settings />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
