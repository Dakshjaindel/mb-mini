import React, { useState } from 'react';
import { ApiError } from './api.js';

export function Card({ title, subtitle, children, actions }) {
  return (
    <section className="card">
      {(title || actions) && (
        <header className="card-head">
          <div>
            {title && <h2>{title}</h2>}
            {subtitle && <p className="muted">{subtitle}</p>}
          </div>
          {actions}
        </header>
      )}
      <div className="card-body">{children}</div>
    </section>
  );
}

export function Field({ label, hint, children }) {
  return (
    <label className="field">
      <span className="field-label">{label}</span>
      {children}
      {hint && <span className="field-hint">{hint}</span>}
    </label>
  );
}

export function Input(props) {
  return <input className="input" {...props} />;
}

export function Button({ children, variant = 'primary', loading, ...rest }) {
  return (
    <button className={`btn btn-${variant}`} disabled={loading || rest.disabled} {...rest}>
      {loading ? '…' : children}
    </button>
  );
}

// Renders the outcome of the last action: an error banner or a result payload.
export function Result({ state }) {
  if (!state) return null;
  if (state.error) {
    const e = state.error;
    const status = e instanceof ApiError && e.status ? ` (HTTP ${e.status})` : '';
    return (
      <div className="result result-error">
        <strong>Failed{status}</strong>
        <pre>{e.message}</pre>
      </div>
    );
  }
  const { data } = state;
  const text = typeof data === 'string' ? data : JSON.stringify(data, null, 2);
  return (
    <div className="result result-ok">
      <strong>Success</strong>
      <pre>{text}</pre>
    </div>
  );
}

// Small helper to wire a submit handler to loading + result/error state.
export function useAction() {
  const [loading, setLoading] = useState(false);
  const [state, setState] = useState(null);
  async function run(fn) {
    setLoading(true);
    try {
      const data = await fn();
      setState({ data });
      return data;
    } catch (error) {
      setState({ error });
      throw error;
    } finally {
      setLoading(false);
    }
  }
  return { loading, state, run, setState };
}
