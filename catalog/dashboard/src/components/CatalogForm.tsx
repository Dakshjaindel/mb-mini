import { useEffect, useState } from 'react'
import type { Catalog, CatalogCreate } from '../types/catalog'

interface CatalogFormProps {
  mode: 'create' | 'edit'
  initial?: Catalog
  onSubmit: (data: CatalogCreate) => Promise<void>
  onCancel: () => void
}

const emptyForm: CatalogCreate = {
  productName: '',
  quantity: 0,
  price: 0,
  isActive: true,
}

export function CatalogForm({ mode, initial, onSubmit, onCancel }: CatalogFormProps) {
  const [form, setForm] = useState<CatalogCreate>(emptyForm)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (initial) {
      setForm({
        productName: initial.productName,
        quantity: initial.quantity,
        price: initial.price,
        isActive: initial.isActive,
      })
    } else {
      setForm(emptyForm)
    }
    setError(null)
  }, [initial, mode])

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setSubmitting(true)
    setError(null)
    try {
      await onSubmit(form)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Something went wrong')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="mb-1.5 block text-sm font-medium text-slate-300">
          Product Name
        </label>
        <input
          type="text"
          required
          value={form.productName}
          onChange={(e) => setForm({ ...form, productName: e.target.value })}
          className="w-full rounded-lg border border-white/10 bg-white/5 px-3 py-2 text-sm text-white outline-none focus:border-sky-500/50 focus:ring-1 focus:ring-sky-500/30"
          placeholder="e.g. Wireless Mouse"
        />
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="mb-1.5 block text-sm font-medium text-slate-300">
            Quantity
          </label>
          <input
            type="number"
            required
            min={0}
            value={form.quantity}
            onChange={(e) => setForm({ ...form, quantity: Number(e.target.value) })}
            className="w-full rounded-lg border border-white/10 bg-white/5 px-3 py-2 text-sm text-white outline-none focus:border-sky-500/50 focus:ring-1 focus:ring-sky-500/30"
          />
        </div>
        <div>
          <label className="mb-1.5 block text-sm font-medium text-slate-300">
            Price ($)
          </label>
          <input
            type="number"
            required
            min={0}
            step="0.01"
            value={form.price}
            onChange={(e) => setForm({ ...form, price: Number(e.target.value) })}
            className="w-full rounded-lg border border-white/10 bg-white/5 px-3 py-2 text-sm text-white outline-none focus:border-sky-500/50 focus:ring-1 focus:ring-sky-500/30"
          />
        </div>
      </div>

      <label className="flex cursor-pointer items-center gap-2 text-sm text-slate-300">
        <input
          type="checkbox"
          checked={form.isActive}
          onChange={(e) => setForm({ ...form, isActive: e.target.checked })}
          className="h-4 w-4 rounded border-white/20 bg-white/5 text-sky-500 focus:ring-sky-500/30"
        />
        Active in catalog
      </label>

      {error && (
        <p className="rounded-lg border border-red-500/30 bg-red-500/10 px-3 py-2 text-sm text-red-300">
          {error}
        </p>
      )}

      <div className="flex justify-end gap-3 pt-2">
        <button
          type="button"
          onClick={onCancel}
          className="rounded-lg border border-white/10 px-4 py-2 text-sm text-slate-300 transition hover:bg-white/5"
        >
          Cancel
        </button>
        <button
          type="submit"
          disabled={submitting}
          className="rounded-lg bg-sky-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-sky-500 disabled:opacity-50"
        >
          {submitting ? 'Saving…' : mode === 'create' ? 'Add Product' : 'Save Changes'}
        </button>
      </div>
    </form>
  )
}
