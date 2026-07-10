import React, { useMemo, useState } from 'react';
import { geofenceApi } from '../api.js';
import { Card, Field, Input, Button, Result, useAction } from '../ui.jsx';
import LeafletMap from '../LeafletMap.jsx';

const SAMPLE = [
  ['28.4120', '77.0620'],
  ['28.4120', '77.0740'],
  ['28.4030', '77.0740'],
  ['28.4030', '77.0620'],
];

export default function Geofence() {
  const [rows, setRows] = useState(SAMPLE);
  const act = useAction();

  const points = useMemo(
    () =>
      rows
        .map((r) => [Number(r[0]), Number(r[1])])
        .filter((p) => p.every((n) => !Number.isNaN(n))),
    [rows]
  );

  const setCell = (i, j) => (e) =>
    setRows((rs) => rs.map((r, idx) => (idx === i ? r.map((c, jdx) => (jdx === j ? e.target.value : c)) : r)));
  const addRow = () => setRows((rs) => [...rs, ['', '']]);
  const removeRow = (i) => setRows((rs) => rs.filter((_, idx) => idx !== i));

  function submit() {
    act.run(() => geofenceApi.setFence(points));
  }

  return (
    <div className="page-grid two">
      <Card
        title="Define delivery geofence"
        subtitle="POST /setFence — validates the polygon of [lat, lng] points"
        actions={<Button variant="ghost" onClick={addRow}>+ Point</Button>}
      >
        <table className="grid">
          <thead><tr><th>#</th><th>Latitude</th><th>Longitude</th><th></th></tr></thead>
          <tbody>
            {rows.map((r, i) => (
              <tr key={i}>
                <td>{i + 1}</td>
                <td><Input value={r[0]} onChange={setCell(i, 0)} placeholder="28.40" /></td>
                <td><Input value={r[1]} onChange={setCell(i, 1)} placeholder="77.06" /></td>
                <td><Button variant="ghost" onClick={() => removeRow(i)}>✕</Button></td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="btn-row" style={{ marginTop: 14 }}>
          <Button loading={act.loading} onClick={submit} disabled={points.length < 3}>Validate &amp; set fence</Button>
        </div>
        {points.length < 3 && <p className="muted">A polygon needs at least 3 points.</p>}
        <Result state={act.state} />
      </Card>

      <Card title="Preview" subtitle={`${points.length} vertex/vertices`}>
        <LeafletMap points={points} mode="polygon" />
      </Card>
    </div>
  );
}
