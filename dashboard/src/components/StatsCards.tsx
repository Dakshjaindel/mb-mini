import type { Catalog } from '../types/catalog'

interface StatsCardsProps {
  catalogs: Catalog[]
}

export function StatsCards({ catalogs }: StatsCardsProps) {
  const total = catalogs.length
  const active = catalogs.filter((c) => c.isActive).length
  const totalValue = catalogs.reduce((sum, c) => sum + c.price * c.quantity, 0)
  const totalUnits = catalogs.reduce((sum, c) => sum + c.quantity, 0)

  const stats = [
    { label: 'Total Products', value: total.toString(), accent: 'text-sky-400' },
    { label: 'Active Items', value: active.toString(), accent: 'text-emerald-400' },
    { label: 'Total Units', value: totalUnits.toLocaleString(), accent: 'text-violet-400' },
    {
      label: 'Inventory Value',
      value: `$${totalValue.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
      accent: 'text-amber-400',
    },
  ]

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
      {stats.map((stat) => (
        <div
          key={stat.label}
          className="rounded-xl border border-white/10 bg-white/5 p-5 backdrop-blur-sm"
        >
          <p className="text-sm text-slate-400">{stat.label}</p>
          <p className={`mt-2 text-2xl font-semibold tracking-tight ${stat.accent}`}>
            {stat.value}
          </p>
        </div>
      ))}
    </div>
  )
}
