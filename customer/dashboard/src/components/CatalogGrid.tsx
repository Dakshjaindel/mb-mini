import type { Catalog } from '../types/catalog'

interface CatalogGridProps {
  catalogs: Catalog[]
  loading: boolean
  onAddToBasket: (product: Catalog, quantity: number) => void
  addingProductId: number | null
}

export function CatalogGrid({ catalogs, loading, onAddToBasket, addingProductId }: CatalogGridProps) {
  if (loading) {
    return (
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: 6 }).map((_, i) => (
          <div key={i} className="h-48 animate-pulse rounded-2xl bg-white/5" />
        ))}
      </div>
    )
  }

  const activeProducts = catalogs.filter((p) => p.isActive !== false)

  if (activeProducts.length === 0) {
    return (
      <div className="rounded-2xl border border-dashed border-white/10 py-16 text-center">
        <p className="text-slate-400">No products in the catalog yet.</p>
        <p className="mt-1 text-sm text-slate-600">Start the catalog service on port 8080.</p>
      </div>
    )
  }

  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {activeProducts.map((product) => (
        <ProductCard
          key={product.id}
          product={product}
          onAdd={onAddToBasket}
          adding={addingProductId === product.id}
        />
      ))}
    </div>
  )
}

function ProductCard({
  product,
  onAdd,
  adding,
}: {
  product: Catalog
  onAdd: (product: Catalog, quantity: number) => void
  adding: boolean
}) {
  const outOfStock = product.quantity <= 0

  return (
    <article className="flex flex-col rounded-2xl border border-white/10 bg-[#121722] p-5 transition hover:border-sky-500/30">
      <div className="mb-3 flex items-start justify-between gap-2">
        <h3 className="font-semibold text-white">{product.productName}</h3>
        <span
          className={`shrink-0 rounded-full px-2 py-0.5 text-xs ${
            outOfStock
              ? 'bg-red-500/10 text-red-400'
              : 'bg-emerald-500/10 text-emerald-400'
          }`}
        >
          {outOfStock ? 'Out of stock' : `${product.quantity} left`}
        </span>
      </div>
      <p className="mb-4 text-2xl font-bold text-sky-400">
        ₹{Number(product.price).toLocaleString('en-IN')}
      </p>
      <button
        type="button"
        disabled={outOfStock || adding}
        onClick={() => onAdd(product, 1)}
        className="mt-auto w-full rounded-xl bg-white/5 py-2.5 text-sm font-medium text-white transition hover:bg-sky-600 disabled:cursor-not-allowed disabled:opacity-40"
      >
        {adding ? 'Adding…' : 'Add to basket'}
      </button>
    </article>
  )
}
