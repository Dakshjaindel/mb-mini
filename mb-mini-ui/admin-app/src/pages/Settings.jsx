import React, { useState } from 'react';
import { getBaseUrls, setBaseUrl, resetBaseUrls, DEFAULT_URLS } from '../api.js';
import { Card, Field, Input, Button } from '../ui.jsx';

const SERVICES = [
  { key: 'catalog', label: 'Catalog service', port: 8080 },
  { key: 'customer', label: 'Customer service (geofence)', port: 8081 },
  { key: 'cart', label: 'Cart service (routing)', port: 8082 },
];

export default function Settings() {
  const [urls, setUrls] = useState(getBaseUrls());
  const [saved, setSaved] = useState(false);

  function save() {
    SERVICES.forEach((s) => setBaseUrl(s.key, urls[s.key]));
    setUrls(getBaseUrls());
    setSaved(true);
    setTimeout(() => setSaved(false), 2000);
  }
  function reset() { resetBaseUrls(); setUrls(getBaseUrls()); }

  return (
    <Card title="Service endpoints" subtitle="Point the admin app at your running mb-mini services.">
      <div className="notice">
        Admin endpoints are not behind the <code className="inline">/consumer/**</code> auth interceptor,
        so no login is required. Services must have CORS enabled (added to the shared framework config).
      </div>
      {SERVICES.map((s) => (
        <Field key={s.key} label={s.label} hint={`Default: ${DEFAULT_URLS[s.key]}`}>
          <Input
            value={urls[s.key]}
            onChange={(e) => setUrls((u) => ({ ...u, [s.key]: e.target.value }))}
            placeholder={DEFAULT_URLS[s.key]}
          />
        </Field>
      ))}
      <div className="btn-row">
        <Button onClick={save}>{saved ? 'Saved ✓' : 'Save'}</Button>
        <Button variant="ghost" onClick={reset}>Reset to defaults</Button>
      </div>
    </Card>
  );
}
