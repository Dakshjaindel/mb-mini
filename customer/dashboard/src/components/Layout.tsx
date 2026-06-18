import type { ReactNode } from 'react'
import { useAuth } from '../context/AuthContext'

type Tab = 'catalog' | 'basket'

interface LayoutProps {
  activeTab: Tab
  onTabChange: (tab: Tab) => void
  basketCount: number
  children: ReactNode
}

export function Layout({ activeTab, onTabChange, basketCount, children }: LayoutProps) {
  const { session, logout } = useAuth()

  return (
    <div className="min-h-screen bg-[#0b0d12] text-slate-200">
      <header className="sticky top-0 z-40 border-b border-white/10 bg-[#0b0d12]/90 backdrop-blur-md">
        <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-6 py-4">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-sky-500 to-violet-600 text-sm font-bold text-white">
              MB
            </div>
            <div>
              <h1 className="text-lg font-semibold text-white">MB Mini Platform</h1>
              <p className="text-xs text-slate-500">Catalog · Customer · Cart</p>
            </div>
          </div>

          {session && (
            <nav className="hidden items-center gap-1 rounded-xl border border-white/10 bg-white/5 p-1 sm:flex">
              <TabButton active={activeTab === 'catalog'} onClick={() => onTabChange('catalog')}>
                Catalog
              </TabButton>
              <TabButton active={activeTab === 'basket'} onClick={() => onTabChange('basket')}>
                Basket{basketCount > 0 ? ` (${basketCount})` : ''}
              </TabButton>
            </nav>
          )}

          <div className="flex items-center gap-3">
            {session ? (
              <>
                <div className="hidden text-right sm:block">
                  <p className="text-sm font-medium text-white">{session.name ?? 'Customer'}</p>
                  <p className="text-xs text-slate-500">+91 {session.phone}</p>
                </div>
                <button
                  type="button"
                  onClick={() => logout()}
                  className="rounded-lg border border-white/10 px-3 py-2 text-sm text-slate-300 transition hover:bg-white/5"
                >
                  Logout
                </button>
              </>
            ) : (
              <span className="rounded-full border border-amber-500/30 bg-amber-500/10 px-3 py-1 text-xs text-amber-300">
                Sign in to shop
              </span>
            )}
          </div>
        </div>

        {session && (
          <div className="flex gap-1 border-t border-white/5 px-6 py-2 sm:hidden">
            <TabButton active={activeTab === 'catalog'} onClick={() => onTabChange('catalog')} mobile>
              Catalog
            </TabButton>
            <TabButton active={activeTab === 'basket'} onClick={() => onTabChange('basket')} mobile>
              Basket{basketCount > 0 ? ` (${basketCount})` : ''}
            </TabButton>
          </div>
        )}
      </header>

      <main className="mx-auto max-w-7xl px-6 py-8">{children}</main>
    </div>
  )
}

function TabButton({
  active,
  onClick,
  children,
  mobile,
}: {
  active: boolean
  onClick: () => void
  children: ReactNode
  mobile?: boolean
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`rounded-lg px-4 py-2 text-sm font-medium transition ${
        mobile ? 'flex-1' : ''
      } ${
        active
          ? 'bg-sky-600 text-white shadow-lg shadow-sky-900/30'
          : 'text-slate-400 hover:bg-white/5 hover:text-white'
      }`}
    >
      {children}
    </button>
  )
}
