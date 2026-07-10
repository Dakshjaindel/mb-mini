import React, { useState } from 'react';
import CatalogAdmin from './pages/CatalogAdmin.jsx';
import Routing from './pages/Routing.jsx';
import Geofence from './pages/Geofence.jsx';
import Settings from './pages/Settings.jsx';

const TABS = [
  { id: 'catalog', label: 'Catalog' },
  { id: 'routing', label: 'Delivery Routing' },
  { id: 'geofence', label: 'Geofencing' },
  { id: 'settings', label: 'Settings' },
];

export default function App() {
  const [tab, setTab] = useState('catalog');
  return (
    <>
      <header className="app-header">
        <div className="brand">🛠️ Milkbasket <small>Admin</small></div>
        <nav>
          {TABS.map((t) => (
            <button key={t.id} className={tab === t.id ? 'active' : ''} onClick={() => setTab(t.id)}>
              {t.label}
            </button>
          ))}
        </nav>
      </header>
      <main className="page">
        {tab === 'catalog' && <CatalogAdmin />}
        {tab === 'routing' && <Routing />}
        {tab === 'geofence' && <Geofence />}
        {tab === 'settings' && <Settings />}
      </main>
    </>
  );
}
