import React, { useState } from 'react';
import { customerApi } from '../api.js';
import { useAuth } from '../auth.jsx';
import { Card, Field, Input, Button, Result, useAction } from '../ui.jsx';

export default function Account({ onAuthed }) {
  const [mode, setMode] = useState('login');
  return (
    <>
      <div className="tabs">
        <button className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>Log in</button>
        <button className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>Register</button>
      </div>
      {mode === 'login' ? <LoginForm onAuthed={onAuthed} /> : <RegisterForm onAuthed={onAuthed} />}
    </>
  );
}

function LoginForm({ onAuthed }) {
  const { establish } = useAuth();
  const [phoneNo, setPhoneNo] = useState('');
  const [password, setPassword] = useState('');
  const check = useAction();
  const login = useAction();

  return (
    <div className="page-grid two">
      <Card title="Log in" subtitle="Sign in with your registered phone number.">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            login.run(async () => {
              const text = await customerApi.login(phoneNo, password);
              await establish(text);
              onAuthed?.();
              return text;
            });
          }}
        >
          <Field label="Phone number">
            <Input value={phoneNo} onChange={(e) => setPhoneNo(e.target.value)} placeholder="9876543210" required />
          </Field>
          <Field label="Password">
            <Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </Field>
          <div className="btn-row">
            <Button loading={login.loading} type="submit">Log in</Button>
            <Button
              type="button"
              variant="ghost"
              loading={check.loading}
              onClick={() => check.run(() => customerApi.generateLogin(phoneNo))}
            >
              Check phone exists
            </Button>
          </div>
          <Result state={login.state || check.state} />
        </form>
      </Card>
      <Card title="How sign-in works" subtitle="mb-mini returns tokens as text.">
        <p className="muted" style={{ marginTop: 0 }}>
          On success the customer service returns a sentence containing an <code className="inline">AuthKey</code> and a{' '}
          <code className="inline">RefreshToken</code>. This app extracts and stores them, then loads your profile via{' '}
          <code className="inline">/customers/me</code>.
        </p>
        <p className="muted">
          "Check phone exists" calls <code className="inline">/customers/generate_login</code> to confirm a number is registered.
        </p>
      </Card>
    </div>
  );
}

const EMPTY_REG = {
  name: '', phoneNo: '', password: '', email: '',
  houseNo: '', locality: '', city: '', pincode: '',
  latitude: '', longitude: '',
};

function RegisterForm({ onAuthed }) {
  const { establish } = useAuth();
  const [f, setF] = useState(EMPTY_REG);
  const reg = useAction();
  const set = (k) => (e) => setF((s) => ({ ...s, [k]: e.target.value }));

  function num(v) { return v === '' ? null : Number(v); }

  return (
    <Card title="Create an account" subtitle="All fields map to the /customers/register payload.">
      <form
        onSubmit={(e) => {
          e.preventDefault();
          const payload = {
            name: f.name,
            phoneNo: f.phoneNo,
            password: f.password,
            email: f.email,
            houseNo: num(f.houseNo),
            locality: f.locality,
            city: f.city,
            pincode: num(f.pincode),
            latitude: num(f.latitude),
            longitude: num(f.longitude),
          };
          reg.run(async () => {
            const text = await customerApi.register(payload);
            await establish(text);
            onAuthed?.();
            return text;
          });
        }}
      >
        <div className="row">
          <Field label="Name"><Input value={f.name} onChange={set('name')} required /></Field>
          <Field label="Phone number"><Input value={f.phoneNo} onChange={set('phoneNo')} required /></Field>
        </div>
        <div className="row">
          <Field label="Password"><Input type="password" value={f.password} onChange={set('password')} required /></Field>
          <Field label="Email"><Input type="email" value={f.email} onChange={set('email')} required /></Field>
        </div>
        <div className="row">
          <Field label="House no."><Input type="number" value={f.houseNo} onChange={set('houseNo')} /></Field>
          <Field label="Locality"><Input value={f.locality} onChange={set('locality')} /></Field>
        </div>
        <div className="row">
          <Field label="City"><Input value={f.city} onChange={set('city')} /></Field>
          <Field label="Pincode"><Input type="number" value={f.pincode} onChange={set('pincode')} required /></Field>
        </div>
        <div className="row">
          <Field label="Latitude" hint="Optional, used for delivery geofencing"><Input type="number" step="any" value={f.latitude} onChange={set('latitude')} /></Field>
          <Field label="Longitude" hint="Optional"><Input type="number" step="any" value={f.longitude} onChange={set('longitude')} /></Field>
        </div>
        <div className="btn-row">
          <Button loading={reg.loading} type="submit">Register &amp; sign in</Button>
        </div>
        <Result state={reg.state} />
      </form>
    </Card>
  );
}
