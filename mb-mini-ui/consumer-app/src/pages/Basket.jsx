import React, { useState } from 'react';
import { cartApi } from '../api.js';
import { useAuth } from '../auth.jsx';
import { Card, Field, Input, Button, Result, useAction } from '../ui.jsx';

function today() {
  return new Date().toISOString().slice(0, 10);
}

export default function Basket() {
  const { session } = useAuth();
  const authKey = session.authKey;
  const userId = session.user?.id ?? '';

  const create = useAction();
  const additem = useAction();
  const finalize = useAction();
  const credit = useAction();

  const [newBasket, setNewBasket] = useState({ userId: userId, date: today(), flag: 1 });
  const [item, setItem] = useState({ productId: '', quantity: 1 });
  const [basketId, setBasketId] = useState('');
  const [creditForm, setCreditForm] = useState({ creditAmount: '', type: 'CREDIT', flag: 1 });

  return (
    <div className="page-grid two">
      <Card title="Create a basket" subtitle="POST /consumer/baskets">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            const millis = new Date(newBasket.date + 'T00:00:00').getTime();
            create.run(() =>
              cartApi.newBasket(Number(newBasket.userId), millis, Number(newBasket.flag), authKey)
            );
          }}
        >
          <Field label="User ID" hint="Defaults to your logged-in customer id">
            <Input type="number" value={newBasket.userId} onChange={(e) => setNewBasket((s) => ({ ...s, userId: e.target.value }))} required />
          </Field>
          <div className="row">
            <Field label="Delivery date"><Input type="date" value={newBasket.date} onChange={(e) => setNewBasket((s) => ({ ...s, date: e.target.value }))} required /></Field>
            <Field label="Flag"><Input type="number" value={newBasket.flag} onChange={(e) => setNewBasket((s) => ({ ...s, flag: e.target.value }))} /></Field>
          </div>
          <Button loading={create.loading} type="submit">Create basket</Button>
          <Result state={create.state} />
        </form>
      </Card>

      <Card title="Add an item" subtitle="POST /consumer/baskets/itemAdd — uses your session for the user id">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            additem.run(() => cartApi.addItem(Number(item.productId), Number(item.quantity), authKey));
          }}
        >
          <div className="row">
            <Field label="Product ID"><Input type="number" value={item.productId} onChange={(e) => setItem((s) => ({ ...s, productId: e.target.value }))} required /></Field>
            <Field label="Quantity"><Input type="number" min="1" value={item.quantity} onChange={(e) => setItem((s) => ({ ...s, quantity: e.target.value }))} required /></Field>
          </div>
          <Button loading={additem.loading} type="submit">Add to basket</Button>
          <Result state={additem.state} />
        </form>
      </Card>

      <Card title="Finalize basket" subtitle="POST /consumer/baskets/finalize">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            finalize.run(() => cartApi.finalize(Number(basketId), authKey));
          }}
        >
          <Field label="Basket ID"><Input type="number" value={basketId} onChange={(e) => setBasketId(e.target.value)} required /></Field>
          <Button loading={finalize.loading} type="submit">Finalize</Button>
          <Result state={finalize.state} />
        </form>
      </Card>

      <Card title="Add credits" subtitle="POST /consumer/credits">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            credit.run(() =>
              cartApi.addCredit(Number(creditForm.creditAmount), creditForm.type, Number(creditForm.flag), authKey)
            );
          }}
        >
          <div className="row">
            <Field label="Amount"><Input type="number" step="any" value={creditForm.creditAmount} onChange={(e) => setCreditForm((s) => ({ ...s, creditAmount: e.target.value }))} required /></Field>
            <Field label="Type"><Input value={creditForm.type} onChange={(e) => setCreditForm((s) => ({ ...s, type: e.target.value }))} /></Field>
            <Field label="Flag"><Input type="number" value={creditForm.flag} onChange={(e) => setCreditForm((s) => ({ ...s, flag: e.target.value }))} /></Field>
          </div>
          <Button loading={credit.loading} type="submit">Add credit</Button>
          <Result state={credit.state} />
        </form>
      </Card>
    </div>
  );
}
