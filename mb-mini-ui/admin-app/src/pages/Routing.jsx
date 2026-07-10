import React, { useMemo, useState } from 'react';
import { routingApi } from '../api.js';
import { Card, Field, Input, Button, Result, useAction } from '../ui.jsx';
import LeafletMap from '../LeafletMap.jsx';

function today() { return new Date().toISOString().slice(0, 10); }

export default function Routing() {
  const [date, setDate] = useState(today());
  const [algo, setAlgo] = useState('optimalPath');
  const [swap, setSwap] = useState(false);
  const [raw, setRaw] = useState(null);
  const act = useAction();

  function run() {
    act
      .run(() => (algo === 'optimalPath' ? routingApi.optimalPath(date) : routingApi.optimalPath2(date)))
      .then((data) => setRaw(Array.isArray(data) ? data : []))
      .catch(() => setRaw(null));
  }

  // Points come back as [a, b] pairs; allow swapping in case the service returns [lng, lat].
  const points = useMemo(() => {
    if (!raw) return [];
    return raw
      .filter((p) => Array.isArray(p) && p.length >= 2)
      .map((p) => (swap ? [Number(p[1]), Number(p[0])] : [Number(p[0]), Number(p[1])]));
  }, [raw, swap]);

  return (
    <>
      <Card title="Optimal delivery route" subtitle="Solves the delivery route for all baskets on a given date (TSP/VRP via ORS).">
        <div className="toolbar">
          <Field label="Delivery date"><Input type="date" value={date} onChange={(e) => setDate(e.target.value)} /></Field>
          <Field label="Algorithm">
            <select className="input" value={algo} onChange={(e) => setAlgo(e.target.value)}>
              <option value="optimalPath">/optimalPath (route 2)</option>
              <option value="optimalPath2">/optimalPath2 (route 3)</option>
            </select>
          </Field>
          <Button loading={act.loading} onClick={run}>Compute route</Button>
          <label className="pill" style={{ cursor: 'pointer' }}>
            <input type="checkbox" checked={swap} onChange={(e) => setSwap(e.target.checked)} />
            swap lat/lng
          </label>
        </div>
        {act.state?.error && <Result state={act.state} />}
      </Card>

      <div style={{ height: 18 }} />

      {raw && (
        <div className="page-grid two">
          <Card title="Route map" subtitle={`${points.length} stop(s)`}>
            <LeafletMap points={points} mode="route" />
          </Card>
          <Card title="Waypoints" subtitle="Order returned by the solver">
            {points.length === 0 ? (
              <div className="empty">No coordinates returned.</div>
            ) : (
              <div className="coord-list">
                <table className="grid">
                  <thead><tr><th>#</th><th>Lat</th><th>Lng</th></tr></thead>
                  <tbody>
                    {points.map((p, i) => (
                      <tr key={i}><td>{i + 1}</td><td>{p[0]}</td><td>{p[1]}</td></tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </Card>
        </div>
      )}
    </>
  );
}
