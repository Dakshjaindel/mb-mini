import React, { useState } from 'react';
import { customerApi } from '../api.js';
import { useAuth } from '../auth.jsx';
import { Card, Field, Input, Button, Result, useAction } from '../ui.jsx';

export default function Profile() {
  const { session, reloadProfile, refresh } = useAuth();
  const user = session.user || {};
  const id = user.id ?? '';

  const reload = useAction();
  const refreshAct = useAction();
  const profile = useAction();
  const pass = useAction();
  const addr = useAction();

  const [pf, setPf] = useState({ newName: user.name || '', newEmail: user.email || '', newHouseNO: '', newLocality: '', newCity: '', newPincode: '' });
  const [np, setNp] = useState('');
  const [af, setAf] = useState({ newHouseNo: '', newLocality: '', newCity: '', newPincode: '' });

  const num = (v) => (v === '' ? null : Number(v));

  return (
    <div className="page-grid two">
      <Card
        title="My profile"
        subtitle="GET /customers/me"
        actions={
          <div className="btn-row">
            <Button variant="ghost" loading={reload.loading} onClick={() => reload.run(reloadProfile)}>Reload</Button>
            <Button variant="ghost" loading={refreshAct.loading} onClick={() => refreshAct.run(refresh)}>Refresh token</Button>
          </div>
        }
      >
        <dl className="kv">
          <dt>ID</dt><dd>{user.id ?? '—'}</dd>
          <dt>Name</dt><dd>{user.name ?? '—'}</dd>
          <dt>Phone</dt><dd>{user.phoneNo ?? '—'}</dd>
          <dt>Email</dt><dd>{user.email ?? '—'}</dd>
        </dl>
        <p className="muted" style={{ wordBreak: 'break-all' }}>AuthKey: <code className="inline">{session.authKey}</code></p>
        <Result state={reload.state || refreshAct.state} />
      </Card>

      <Card title="Update profile" subtitle="PUT /customers">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            profile.run(() => customerApi.updateProfile({
              customerId: Number(id),
              newName: pf.newName,
              newEmail: pf.newEmail,
              newHouseNO: num(pf.newHouseNO),
              newLocality: pf.newLocality,
              newCity: pf.newCity,
              newPincode: num(pf.newPincode),
            }));
          }}
        >
          <div className="row">
            <Field label="Name"><Input value={pf.newName} onChange={(e) => setPf((s) => ({ ...s, newName: e.target.value }))} /></Field>
            <Field label="Email"><Input value={pf.newEmail} onChange={(e) => setPf((s) => ({ ...s, newEmail: e.target.value }))} /></Field>
          </div>
          <div className="row">
            <Field label="House no."><Input type="number" value={pf.newHouseNO} onChange={(e) => setPf((s) => ({ ...s, newHouseNO: e.target.value }))} /></Field>
            <Field label="Locality"><Input value={pf.newLocality} onChange={(e) => setPf((s) => ({ ...s, newLocality: e.target.value }))} /></Field>
          </div>
          <div className="row">
            <Field label="City"><Input value={pf.newCity} onChange={(e) => setPf((s) => ({ ...s, newCity: e.target.value }))} /></Field>
            <Field label="Pincode"><Input type="number" value={pf.newPincode} onChange={(e) => setPf((s) => ({ ...s, newPincode: e.target.value }))} /></Field>
          </div>
          <Button loading={profile.loading} type="submit">Save profile</Button>
          <Result state={profile.state} />
        </form>
      </Card>

      <Card title="Change password" subtitle="PUT /customers/password">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            pass.run(() => customerApi.updatePassword(Number(id), np));
          }}
        >
          <Field label="New password"><Input type="password" value={np} onChange={(e) => setNp(e.target.value)} required /></Field>
          <Button loading={pass.loading} type="submit">Update password</Button>
          <Result state={pass.state} />
        </form>
      </Card>

      <Card title="Update address" subtitle="PUT /customer/address">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            addr.run(() => customerApi.updateAddress({
              id: Number(id),
              newHouseNo: num(af.newHouseNo),
              newLocality: af.newLocality,
              newCity: af.newCity,
              newPincode: num(af.newPincode),
            }));
          }}
        >
          <div className="row">
            <Field label="House no."><Input type="number" value={af.newHouseNo} onChange={(e) => setAf((s) => ({ ...s, newHouseNo: e.target.value }))} /></Field>
            <Field label="Locality"><Input value={af.newLocality} onChange={(e) => setAf((s) => ({ ...s, newLocality: e.target.value }))} /></Field>
          </div>
          <div className="row">
            <Field label="City"><Input value={af.newCity} onChange={(e) => setAf((s) => ({ ...s, newCity: e.target.value }))} /></Field>
            <Field label="Pincode"><Input type="number" value={af.newPincode} onChange={(e) => setAf((s) => ({ ...s, newPincode: e.target.value }))} /></Field>
          </div>
          <Button loading={addr.loading} type="submit">Save address</Button>
          <Result state={addr.state} />
        </form>
      </Card>
    </div>
  );
}
