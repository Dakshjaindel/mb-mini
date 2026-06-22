import type { Catalog } from '../types/catalog'

interface CatalogTableProps {
  catalogs: Catalog[]
  onEdit: (catalog: Catalog) => void
  loading: boolean
}

export function CatalogTable({ catalogs, onEdit, loading }: CatalogTableProps) {
  if (loading) {
    return (
      <div className="flex h-48 items-center justify-center rounded-xl border border-white/10 bg-white/5">
        <div className="flex items-center gap-3 text-slate-400">
          <span className="h-5 w-5 animate-spin rounded-full border-2 border-slate-600 border-t-sky-400" />
          Loading catalog…
        </div>
      </div>
    )
  }

  if (catalogs.length === 0) {
    return (
      <div className="flex h-48 flex-col items-center justify-center rounded-xl border border-dashed border-white/15 bg-white/5 text-center">
        <p className="text-lg font-medium text-slate-300">No products yet</p>
        <p className="mt-1 text-sm text-slate-500">Add your first catalog item to get started.</p>
      </div>
    )
  }

  return (
    <div className="overflow-hidden rounded-xl border border-white/10 bg-white/5">
      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-white/10 bg-white/5 text-xs uppercase tracking-wider text-slate-400">
              <th className="px-5 py-3 font-medium">ID</th>
              <th className="px-5 py-3 font-medium">Product</th>
              <th className="px-5 py-3 font-medium">Qty</th>
              <th className="px-5 py-3 font-medium">Price</th>
              <th className="px-5 py-3 font-medium">Value</th>
              <th className="px-5 py-3 font-medium">Status</th>
              <th className="px-5 py-3 font-medium text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/5">
            {catalogs.map((catalog) => (
              <tr key={catalog.id} className="transition hover:bg-white/[0.03]">
                <td className="px-5 py-3.5 font-mono text-slate-400">#{catalog.id}</td>
                <td className="px-5 py-3.5 font-medium text-white">{catalog.productName}</td>
                <td className="px-5 py-3.5 text-slate-300">{catalog.quantity}</td>
                <td className="px-5 py-3.5 text-slate-300">
                  ${Number(catalog.price).toFixed(2)}
                </td>
                <td className="px-5 py-3.5 text-slate-300">
                  ${(Number(catalog.price) * catalog.quantity).toFixed(2)}
                </td>
                <td className="px-5 py-3.5">
                  <span
                    className={`inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium ${
                      catalog.isActive
                        ? 'bg-emerald-500/15 text-emerald-400'
                        : 'bg-slate-500/15 text-slate-400'
                    }`}
                  >
                    {catalog.isActive ? 'Active' : 'Inactive'}
                  </span>
                </td>
                <td className="px-5 py-3.5 text-right">
                  <button
                    type="button"
                    onClick={() => onEdit(catalog)}
                    className="rounded-md border border-white/10 px-3 py-1.5 text-xs text-slate-300 transition hover:border-sky-500/30 hover:text-sky-400"
                  >
                    Edit
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
