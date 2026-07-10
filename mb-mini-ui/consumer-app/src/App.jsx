import React, { useState } from 'react';
import { useAuth } from './auth.jsx';
import { Button } from './ui.jsx';
import Account from './pages/Account.jsx';
import Catalog from './pages/Catalog.jsx';
import Basket from './pages/Basket.jsx';
import Profile from './pages/Profile.jsx';
import Settings from './pages/Settings.jsx';

const TABS = [
  { id: 'catalog', label: 'Shop', needsAuth: false },
  { id: 'basket', label: 'My Basket', needsAuth: true },
  { id: 'profile', label: 'Profile', needsAuth: true },
  { id: 'account', label: 'Sign in', needsAuth: false, hideWhenAuthed: true },
  { id: 'settings', label: 'Settings', needsAuth: false },
];

export default function App() {
  const { session, isAuthed, logout } = useAuth();
  const [tab, setTab] = useState('catalog');

  const visibleTabs = TABS.filter((t) => !(t.hideWhenAuthed && isAuthed));

  function go(id) {
    const t = TABS.find((x) => x.id === id);
    if (t?.needsAuth && !isAuthed) setTab('account');
    else setTab(id);
  }

  const who = session?.user?.name || session?.user?.phoneNo;

  return (
    <>
      <header className="app-header">
        <div className="brand">🥛 Milkbasket <small>Consumer</small></div>
        <nav>
          {visibleTabs.map((t) => (
            <button
              key={t.id}
              className={tab === t.id ? 'active' : ''}
              onClick={() => go(t.id)}
            >
              {t.label}
            </button>
          ))}
        </nav>
        <div className="spacer" />
        {isAuthed && (
          <div className="session-chip">
            <span className="who">👤 {who || 'signed in'}</span>
            <Button variant="ghost" onClick={logout} style={{ background: 'rgba(255,255,255,0.15)', color: '#fff', border: 0 }}>
              Log out
            </Button>
          </div>
        )}
      </header>

      <main className="page">
        {tab === 'catalog' && <Catalog />}
        {tab === 'basket' && (isAuthed ? <Basket /> : <NeedAuth onGo={() => setTab('account')} />)}
        {tab === 'profile' && (isAuthed ? <Profile /> : <NeedAuth onGo={() => setTab('account')} />)}
        {tab === 'account' && <Account onAuthed={() => setTab('catalog')} />}
        {tab === 'settings' && <Settings />}
      </main>
    </>
  );
}

function NeedAuth({ onGo }) {
  return (
    <div className="empty">
      <p>Please sign in to use this section.</p>
      <Button onClick={onGo}>Go to sign in</Button>
    </div>
  );
}
