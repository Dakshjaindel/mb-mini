import React, { useEffect, useState } from 'react';
import { catalogApi, cartApi } from '../api.js';
import { useAuth } from '../auth.jsx';
import { Card, Field, Input, Button, Result, useAction } from '../ui.jsx';

const DEFAULT_FILTERS = { pageSize: 20, pageNo: 0, similar: '', productNameFilter: '', quantityFilter: '' };

export default function Catalog() {
  const { isAuthed, session } = useAuth();
  const [filters, setFilters] = useState(DEFAULT_FILTERS);
  const [items, setItems] = useState(null);
  const list = useAction();
  const addAction = useAction();

  const set = (k) => (e) => setFilters((s) => ({ ...s, [k]: e.target.value }));

  async function load() {
    const payload = {
      pageSize: Number(filters.pageSize) || 20,
      pageNo: Number(filters.pageNo) || 0,
      similar: filters.similar || null,
      productNameFilter: filters.productNameFilter || null,
      quantityFilter: filters.quantityFilter || null,
    };
    const data = await list.run(() => catalogApi.list(payload));
    setItems(Array.isArray(data) ? data : []);
  }

  useEffect(() => {
    load().catch(() => {});
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function addToBasket(item) {
    addAction.run(() => cartApi.addItem(item.id ?? item.Id, 1, session.authKey));
  }

  return (
    <>
      <Card title="Browse catalog" subtitle="GET /catalog/all — filter and paginate the product list.">
        <form
          onSubmit={(e) => { e.preventDefault(); load().catch(() => {}); }}
        >
          <div className="row">
            <Field label="Name contains"><Input value={filters.productNameFilter} onChange={set('productNameFilter')} placeholder="milk" /></Field>
            <Field label="Similar to"><Input value={filters.similar} onChange={set('similar')} placeholder="fuzzy match" /></Field>
            <Field label="Quantity filter"><Input value={filters.quantityFilter} onChange={set('quantityFilter')} placeholder="e.g. >0" /></Field>
          </div>
          <div className="row">
            <Field label="Page size"><Input type="number" value={filters.pageSize} onChange={set('pageSize')} /></Field>
            <Field label="Page no."><Input type="number" value={filters.pageNo} onChange={set('pageNo')} /></Field>
            <div style={{ display: 'flex', alignItems: 'flex-end' }}>
              <Button loading={list.loading} type="submit">Search</Button>
            </div>
          </div>
        </form>
        {list.state?.error && <Result state={list.state} />}
        {addAction.state && <Result state={addAction.state} />}
      </Card>

      <div style={{ height: 18 }} />

      <Card title={items ? `${items.length} product(s)` : 'Products'}>
        {!items && <div className="empty">Loading…</div>}
        {items && items.length === 0 && <div className="empty">No products found.</div>}
        {items && items.length > 0 && (
          <table className="grid">
            <thead>
              <tr>
                <th>ID</th><th>Product</th><th>Qty</th><th>Price</th><th>Status</th>
                {isAuthed && <th></th>}
              </tr>
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
                    <td>
                      <span className={`badge ${it.isActive ? 'on' : 'off'}`}>
                        {it.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    {isAuthed && (
                      <td>
                        <Button variant="ghost" onClick={() => addToBasket(it)} loading={addAction.loading}>
                          + Basket
                        </Button>
                      </td>
                    )}
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
