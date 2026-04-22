import React, { createContext, useContext, useState } from 'react';
import { setToken, getToken } from './api.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem('atb_user');
    return raw ? JSON.parse(raw) : null;
  });

  const login = async (username, password) => {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });
    if (!res.ok) {
      const msg = (await res.json().catch(() => ({}))).error || 'login failed';
      throw new Error(msg);
    }
    const data = await res.json();
    setToken(data.token);
    localStorage.setItem('atb_user', JSON.stringify(data.user));
    setUser(data.user);
  };

  const logout = () => {
    setToken(null);
    localStorage.removeItem('atb_user');
    setUser(null);
  };

  const isAuthed = () => !!getToken() && !!user;

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthed }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
