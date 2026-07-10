import React, { useEffect, useState } from 'react';
import { catalogApi } from '../api.js';
import { Card, Field, Input, Button, Result, useAction } from '../ui.jsx';

const EMPTY = { productName: '', quantity: '', price: '', isActive: true };

export default function CatalogAdmin() {
  const [items, setItems] = useState(null);
  const [form, setForm] = useState(EMPTY);
  const [editId, setEditId] = useState(null);
  const [qty, setQty] = useState({ productId: '', quantity: '' });

  const listAct = useAction();
  const saveAct = useAction();
  const qtyAct = useAction();
  const cacheAct = useAction();

  async function load() {
    const data = await listAct.run(() =>
      catalogApi.listAll({ pageSize: 100, pageNo: 1, similar: null, productNameFilter: null, quantityFilter: null })
    );
    setItems(Array.isArray(data) ? data : []);
  }

  useEffect(() => { load().catch(() => {}); /* eslint-disable-next-line */ }, []);

  const set = (k) => (e) => {
    const v = k === 'isActive' ? e.target.checked : e.target.value;
    setForm((s) => ({ ...s, [k]: v }));
  };

  function startEdit(it) {
    setEditId(it.id ?? it.Id);
    setForm({ productName: it.productName, quantity: it.quantity, price: it.price, isActive: !!it.isActive });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  function resetForm() { setEditId(null); setForm(EMPTY); }

  function submit(e) {
    e.preventDefault();
    const base = {
      productName: form.productName,
      quantity: Number(form.quantity),
      price: Number(form.price),
      isActive: !!form.isActive,
    };
    saveAct
      .run(() => (editId == null ? catalogApi.create(base) : catalogApi.update({ id: Number(editId), ...base })))
      .then(() => { resetForm(); load().catch(() => {}); })
      .catch(() => {});
  }

  return (
    <>
      <div className="page-grid two">
        <Card
          title={editId == null ? 'Add product' : `Edit product #${editId}`}
          subtitle={editId == null ? 'POST /catalog' : 'PUT /catalog'}
          actions={editId != null && <Button variant="ghost" onClick={resetForm}>New</Button>}
        >
          <form onSubmit={submit}>
            <Field label="Product name"><Input value={form.productName} onChange={set('productName')} required /></Field>
            <div className="row">
              <Field label="Quantity"><Input type="number" value={form.quantity} onChange={set('quantity')} required /></Field>
              <Field label="Price"><Input type="number" step="any" value={form.price} onChange={set('price')} required /></Field>
            </div>
            <label className="field" style={{ flexDirection: 'row', alignItems: 'center', gap: 8 }}>
              <input type="checkbox" checked={form.isActive} onChange={set('isActive')} />
              <span className="field-label" style={{ margin: 0 }}>Active</span>
            </label>
            <div className="btn-row">
              <Button loading={saveAct.loading} type="submit">{editId == null ? 'Create' : 'Save changes'}</Button>
            </div>
            <Result state={saveAct.state} />
          </form>
        </Card>

        <div className="page-grid">
          <Card title="Quick quantity update" subtitle="PUT /catalog/quantity">
            <form
              onSubmit={(e) => {
                e.preventDefault();
                qtyAct
                  .run(() => catalogApi.updateQuantity(Number(qty.productId), Number(qty.quantity)))
                  .then(() => load().catch(() => {}))
                  .catch(() => {});
              }}
            >
              <div className="row">
                <Field label="Product ID"><Input type="number" value={qty.productId} onChange={(e) => setQty((s) => ({ ...s, productId: e.target.value }))} required /></Field>
                <Field label="New quantity"><Input type="number" value={qty.quantity} onChange={(e) => setQty((s) => ({ ...s, quantity: e.target.value }))} required /></Field>
              </div>
              <Button loading={qtyAct.loading} type="submit">Update quantity</Button>
              <Result state={qtyAct.state} />
            </form>
          </Card>

          <Card title="Cache" subtitle='POST "/consumer /catalog/cache/refresh"'>
            <p className="muted" style={{ marginTop: 0 }}>Refresh the catalog cache in Redis.</p>
            <Button variant="ghost" loading={cacheAct.loading} onClick={() => cacheAct.run(() => catalogApi.cacheRefresh())}>
              Refresh cache
            </Button>
            <Result state={cacheAct.state} />
          </Card>
        </div>
      </div>

      <div style={{ height: 18 }} />

      <Card
        title="Catalog"
        subtitle="GET /catalog/all"
        actions={<Button variant="ghost" loading={listAct.loading} onClick={() => load().catch(() => {})}>Reload</Button>}
      >
        {listAct.state?.error && <Result state={listAct.state} />}
        {!items && !listAct.state?.error && <div className="empty">Loading…</div>}
        {items && items.length === 0 && <div className="empty">No products yet.</div>}
        {items && items.length > 0 && (
          <table className="grid">
            <thead>
              <tr><th>ID</th><th>Product</th><th>Qty</th><th>Price</th><th>Status</th><th></th></tr>
            </thead>
            <tbody>
              {items.map((it) => {
                const id = it.id ?? it.Id;
                return (
                  <tr key={id}>
                    <td>{id}</td>
                    <td>{it.productName}</td>
                    <td>{it.quantity}</td>
                    <td className="price">₹{it.price}</td>
                    <td><span className={`badge ${it.isActive ? 'on' : 'off'}`}>{it.isActive ? 'Active' : 'Inactive'}</span></td>
                    <td><Button variant="ghost" onClick={() => startEdit(it)}>Edit</Button></td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </Card>
    </>
  );
}
