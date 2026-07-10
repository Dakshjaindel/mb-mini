import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { customerApi, parseSession } from './api.js';

const SESSION_KEY = 'mbmini.consumer.session';
const AuthContext = createContext(null);

function loadSession() {
  try {
    const raw = localStorage.getItem(SESSION_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [session, setSession] = useState(loadSession);

  useEffect(() => {
    if (session) localStorage.setItem(SESSION_KEY, JSON.stringify(session));
    else localStorage.removeItem(SESSION_KEY);
  }, [session]);

  const actions = useMemo(
    () => ({
      // Accepts the raw server sentence, extracts tokens, and (best-effort) loads the profile.
      async establish(rawText) {
        const { authKey, refreshToken } = parseSession(rawText);
        if (!authKey) {
          throw new Error('Could not find an AuthKey in the server response: ' + rawText);
        }
        let user = null;
        try {
          user = await customerApi.me(authKey);
        } catch {
          /* profile is optional right after login */
        }
        setSession({ authKey, refreshToken, user });
        return { authKey, refreshToken, user };
      },
      async reloadProfile() {
        if (!session?.authKey) return null;
        const user = await customerApi.me(session.authKey);
        setSession((s) => ({ ...s, user }));
        return user;
      },
      async refresh() {
        if (!session?.refreshToken) throw new Error('No refresh token stored.');
        const text = await customerApi.refresh(session.refreshToken);
        const { authKey, refreshToken } = parseSession(text);
        if (!authKey) throw new Error('Refresh did not return a new AuthKey: ' + text);
        setSession((s) => ({
          ...s,
          authKey,
          refreshToken: refreshToken || s.refreshToken,
        }));
        return text;
      },
      async logout() {
        try {
          if (session?.authKey) await customerApi.logout(session.authKey);
        } finally {
          setSession(null);
        }
      },
      clear() {
        setSession(null);
      },
    }),
    [session]
  );

  const value = useMemo(
    () => ({ session, isAuthed: !!session?.authKey, ...actions }),
    [session, actions]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
