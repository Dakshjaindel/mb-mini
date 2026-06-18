import { useState } from 'react'
import type { LocalBasketItem } from '../types/basket'

interface BasketPanelProps {
  items: LocalBasketItem[]
  basketId: number | null
  finalized: boolean
  loading: boolean
  walletCredit: number
  onEnsureBasket: () => Promise<number>
  onAddCredit: (amount: number) => Promise<void>
  onUpdateQuantity: (productId: number, quantity: number) => Promise<void>
  onFinalize: () => Promise<void>
  onNewBasket: () => void
}

export function BasketPanel({
  items,
  basketId,
  finalized,
  loading,
  walletCredit,
  onEnsureBasket,
  onAddCredit,
  onUpdateQuantity,
  onFinalize,
  onNewBasket,
}: BasketPanelProps) {
  const [creditAmount, setCreditAmount] = useState('500')
  const [busy, setBusy] = useState(false)

  const subtotal = items.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const remainingCredit = Math.max(0, walletCredit - subtotal)

  async function run(action: () => Promise<void>) {
    setBusy(true)
    try {
      await action()
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
      <section className="rounded-2xl border border-white/10 bg-[#121722]">
        <div className="flex items-center justify-between border-b border-white/10 px-6 py-4">
          <div>
            <h2 className="text-lg font-semibold text-white">Your basket</h2>
            <p className="text-sm text-slate-500">
              {basketId ? `Basket #${basketId}` : 'No active basket'}
              {finalized && ' · Order placed'}
            </p>
          </div>
          {finalized && (
            <button
              type="button"
              onClick={onNewBasket}
              className="rounded-lg bg-sky-600 px-3 py-1.5 text-sm text-white hover:bg-sky-500"
            >
              New order
            </button>
          )}
        </div>

        {items.length === 0 ? (
          <p className="px-6 py-12 text-center text-slate-500">
            Your basket is empty. Add products from the catalog.
          </p>
        ) : (
          <ul className="divide-y divide-white/5">
            {items.map((item) => (
              <li key={item.productId} className="flex items-center justify-between gap-4 px-6 py-4">
                <div>
                  <p className="font-medium text-white">{item.productName}</p>
                  <p className="text-sm text-slate-500">₹{item.price.toLocaleString('en-IN')} each</p>
                </div>
                <div className="flex items-center gap-3">
                  {!finalized && (
                    <div className="flex items-center rounded-lg border border-white/10">
                      <QtyButton
                        label="−"
                        onClick={() => run(() => onUpdateQuantity(item.productId, item.quantity - 1))}
                        disabled={busy || loading}
                      />
                      <span className="min-w-[2rem] text-center text-sm">{item.quantity}</span>
                      <QtyButton
                        label="+"
                        onClick={() => run(() => onUpdateQuantity(item.productId, item.quantity + 1))}
                        disabled={busy || loading}
                      />
                    </div>
                  )}
                  <span className="min-w-[4rem] text-right font-semibold text-sky-400">
                    ₹{(item.price * item.quantity).toLocaleString('en-IN')}
                  </span>
                </div>
              </li>
            ))}
          </ul>
        )}

        {items.length > 0 && (
          <div className="flex items-center justify-between border-t border-white/10 px-6 py-4">
            <span className="text-slate-400">Subtotal</span>
            <span className="text-xl font-bold text-white">₹{subtotal.toLocaleString('en-IN')}</span>
          </div>
        )}
      </section>

      <aside className="space-y-4">
        <div className="rounded-2xl border border-white/10 bg-[#121722] p-5">
          <div className="flex items-start justify-between gap-3">
            <div>
              <h3 className="font-semibold text-white">Wallet</h3>
              <p className="mt-1 text-xs text-slate-500">
                Basket service checks credit before items are added.
              </p>
            </div>
            <span className="rounded-lg bg-emerald-500/10 px-2 py-1 text-sm font-semibold text-emerald-300">
              ₹{walletCredit.toLocaleString('en-IN')}
            </span>
          </div>
          {items.length > 0 && (
            <div className="mt-4 rounded-xl border border-white/10 bg-[#0b0d12] p-3 text-xs">
              <div className="flex justify-between text-slate-400">
                <span>Basket subtotal</span>
                <span>₹{subtotal.toLocaleString('en-IN')}</span>
              </div>
              <div className="mt-2 flex justify-between text-slate-300">
                <span>After checkout</span>
                <span>₹{remainingCredit.toLocaleString('en-IN')}</span>
              </div>
            </div>
          )}
          <p className="mt-1 text-xs text-slate-500">
            Creating a basket happens automatically before the first top-up.
          </p>
          <div className="mt-4 flex gap-2">
            <input
              type="number"
              min="1"
              value={creditAmount}
              onChange={(e) => setCreditAmount(e.target.value)}
              className="w-full rounded-xl border border-white/10 bg-[#0b0d12] px-3 py-2 text-sm text-white outline-none"
            />
            <button
              type="button"
              disabled={busy || finalized}
              onClick={() =>
                run(async () => {
                  await onEnsureBasket()
                  await onAddCredit(Number(creditAmount))
                })
              }
              className="shrink-0 rounded-xl bg-violet-600 px-4 py-2 text-sm font-medium text-white hover:bg-violet-500 disabled:opacity-50"
            >
              Add
            </button>
          </div>
        </div>

        <div className="rounded-2xl border border-white/10 bg-[#121722] p-5">
          <h3 className="font-semibold text-white">Checkout</h3>
          <p className="mt-1 text-xs text-slate-500">
            Finalizing charges your wallet, reduces stock, and marks the basket as complete.
          </p>
          <button
            type="button"
            disabled={busy || loading || finalized || items.length === 0 || walletCredit < subtotal}
            onClick={() =>
              run(async () => {
                await onEnsureBasket()
                await onFinalize()
              })
            }
            className="mt-4 w-full rounded-xl bg-emerald-600 py-3 text-sm font-semibold text-white transition hover:bg-emerald-500 disabled:cursor-not-allowed disabled:opacity-40"
          >
            {finalized ? 'Order placed' : busy ? 'Processing…' : 'Finalize order'}
          </button>
          {items.length > 0 && walletCredit < subtotal && !finalized && (
            <p className="mt-2 text-xs text-amber-300">
              Add ₹{(subtotal - walletCredit).toLocaleString('en-IN')} more credit to checkout.
            </p>
          )}
        </div>

        <ServiceStatus />
      </aside>
    </div>
  )
}

function QtyButton({
  label,
  onClick,
  disabled,
}: {
  label: string
  onClick: () => void
  disabled: boolean
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      className="px-3 py-1 text-slate-300 hover:bg-white/5 disabled:opacity-40"
    >
      {label}
    </button>
  )
}

function ServiceStatus() {
  const services = [
    { name: 'Catalog', port: 8080, color: 'sky' },
    { name: 'Customer', port: 8081, color: 'violet' },
    { name: 'Cart', port: 8082, color: 'emerald' },
  ]

  return (
    <div className="rounded-2xl border border-white/10 bg-[#121722] p-5">
      <h3 className="text-sm font-semibold text-white">Connected services</h3>
      <ul className="mt-3 space-y-2">
        {services.map((s) => (
          <li key={s.name} className="flex items-center justify-between text-xs">
            <span className="text-slate-400">{s.name}</span>
            <span className="text-slate-500">localhost:{s.port}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}
