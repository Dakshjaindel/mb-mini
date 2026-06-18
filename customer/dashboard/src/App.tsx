import { useCallback, useEffect, useState } from 'react'
import { fetchCatalogs } from './api/catalog'
import * as cartApi from './api/cart'
import { AuthPanel } from './components/AuthPanel'
import { BasketPanel } from './components/BasketPanel'
import { CatalogGrid } from './components/CatalogGrid'
import { Layout } from './components/Layout'
import { Toast } from './components/Toast'
import { AuthProvider, useAuth } from './context/AuthContext'
import { useToast } from './hooks/useToast'
import type { LocalBasketItem } from './types/basket'
import type { Catalog } from './types/catalog'

type Tab = 'catalog' | 'basket'

function Dashboard() {
  const { session, loading: authLoading } = useAuth()
  const { toast, showToast, dismissToast } = useToast()

  const [activeTab, setActiveTab] = useState<Tab>('catalog')
  const [catalogs, setCatalogs] = useState<Catalog[]>([])
  const [catalogLoading, setCatalogLoading] = useState(false)
  const [addingProductId, setAddingProductId] = useState<number | null>(null)

  const [basketId, setBasketId] = useState<number | null>(null)
  const [basketItems, setBasketItems] = useState<LocalBasketItem[]>([])
  const [basketFinalized, setBasketFinalized] = useState(false)
  const [basketBusy, setBasketBusy] = useState(false)
  const [walletCredit, setWalletCredit] = useState(0)

  const loadCatalogs = useCallback(async () => {
    setCatalogLoading(true)
    try {
      const data = await fetchCatalogs()
      setCatalogs(data)
    } catch (err) {
      showToast(err instanceof Error ? err.message : 'Failed to load catalog', 'error')
    } finally {
      setCatalogLoading(false)
    }
  }, [showToast])

  useEffect(() => {
    if (session) {
      loadCatalogs()
    }
  }, [session, loadCatalogs])

  useEffect(() => {
    setBasketId(null)
    setBasketItems([])
    setBasketFinalized(false)
    setWalletCredit(0)
  }, [session?.customerId])

  const ensureBasket = useCallback(async (): Promise<number> => {
    if (basketId) return basketId
    if (!session) throw new Error('Not signed in')
    const id = await cartApi.createBasket(session.customerId)
    setBasketId(id)
    setBasketFinalized(false)
    return id
  }, [basketId, session])

  const syncLocalItem = useCallback((product: Catalog, quantity: number) => {
    setBasketItems((prev) => {
      if (quantity <= 0) {
        return prev.filter((item) => item.productId !== product.id)
      }
      const existing = prev.find((item) => item.productId === product.id)
      if (existing) {
        return prev.map((item) =>
          item.productId === product.id ? { ...item, quantity } : item,
        )
      }
      return [
        ...prev,
        {
          productId: product.id,
          productName: product.productName,
          price: Number(product.price),
          quantity,
        },
      ]
    })
  }, [])

  async function handleAddToBasket(product: Catalog, quantity: number) {
    if (!session) {
      showToast('Sign in to add items to your basket', 'error')
      return
    }
    setAddingProductId(product.id)
    setBasketBusy(true)
    try {
      const id = await ensureBasket()
      const existing = basketItems.find((item) => item.productId === product.id)
      const newQty = (existing?.quantity ?? 0) + quantity
      const lineTotal = Number(product.price) * newQty
      if (walletCredit < lineTotal) {
        showToast(
          `Add at least ₹${lineTotal.toLocaleString('en-IN')} wallet credit before adding this item.`,
          'error',
        )
        setActiveTab('basket')
        return
      }
      await cartApi.addItemToBasket(id, product.id, newQty)
      syncLocalItem(product, newQty)
      showToast(`Added ${product.productName} to basket`)
      setActiveTab('basket')
    } catch (err) {
      showToast(err instanceof Error ? err.message : 'Could not add item', 'error')
    } finally {
      setAddingProductId(null)
      setBasketBusy(false)
    }
  }

  async function handleUpdateQuantity(productId: number, quantity: number) {
    if (!basketId) return
    const item = basketItems.find((i) => i.productId === productId)
    if (!item) return

    setBasketBusy(true)
    try {
      await cartApi.addItemToBasket(basketId, productId, quantity)
      syncLocalItem(
        { id: productId, productName: item.productName, price: item.price, quantity: 0, isActive: true },
        quantity,
      )
    } catch (err) {
      showToast(err instanceof Error ? err.message : 'Could not update quantity', 'error')
    } finally {
      setBasketBusy(false)
    }
  }

  async function handleAddCredit(amount: number) {
    if (!session) return
    if (!amount || amount <= 0) {
      showToast('Enter a valid credit amount', 'error')
      return
    }
    await ensureBasket()
    await cartApi.addWalletCredit(session.customerId, amount)
    setWalletCredit((current) => current + amount)
    showToast(`Added ₹${amount.toLocaleString('en-IN')} to wallet`)
  }

  async function handleFinalize() {
    if (!basketId) throw new Error('No basket')
    const message = await cartApi.finalizeOrder(basketId)
    setBasketFinalized(true)
    const total = basketItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
    setWalletCredit((current) => Math.max(0, current - total))
    showToast(message)
    await loadCatalogs()
  }

  function handleNewBasket() {
    setBasketId(null)
    setBasketItems([])
    setBasketFinalized(false)
    setWalletCredit(0)
    setActiveTab('catalog')
  }

  const basketCount = basketItems.reduce((sum, item) => sum + item.quantity, 0)

  if (authLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-[#0b0d12] text-slate-400">
        Loading session…
      </div>
    )
  }

  if (!session) {
    return (
      <div className="min-h-screen bg-[#0b0d12] py-10">
        <div className="mb-8 text-center">
          <p className="text-sm font-semibold uppercase tracking-widest text-sky-400">MB Mini</p>
          <h1 className="mt-2 text-3xl font-bold text-white">Unified Commerce Dashboard</h1>
          <p className="mt-2 text-slate-500">Customer · Catalog · Cart — all in one place</p>
        </div>
        <AuthPanel onSuccess={() => showToast('Welcome!')} onError={(msg) => showToast(msg, 'error')} />
        <Toast message={toast?.message ?? null} type={toast?.type} onDismiss={dismissToast} />
      </div>
    )
  }

  return (
    <Layout activeTab={activeTab} onTabChange={setActiveTab} basketCount={basketCount}>
      {activeTab === 'catalog' && (
        <section>
          <div className="mb-6 flex flex-wrap items-end justify-between gap-4">
            <div>
              <h2 className="text-2xl font-bold text-white">Product catalog</h2>
              <p className="mt-1 text-sm text-slate-500">
                Live from catalog service · {catalogs.length} products
              </p>
            </div>
            <button
              type="button"
              onClick={loadCatalogs}
              className="rounded-lg border border-white/10 px-4 py-2 text-sm text-slate-300 hover:bg-white/5"
            >
              Refresh
            </button>
          </div>
          <CatalogGrid
            catalogs={catalogs}
            loading={catalogLoading}
            onAddToBasket={handleAddToBasket}
            addingProductId={addingProductId}
          />
        </section>
      )}

      {activeTab === 'basket' && (
        <BasketPanel
          items={basketItems}
          basketId={basketId}
          finalized={basketFinalized}
          loading={basketBusy}
          walletCredit={walletCredit}
          onEnsureBasket={ensureBasket}
          onAddCredit={handleAddCredit}
          onUpdateQuantity={handleUpdateQuantity}
          onFinalize={handleFinalize}
          onNewBasket={handleNewBasket}
        />
      )}

      <Toast message={toast?.message ?? null} type={toast?.type} onDismiss={dismissToast} />
    </Layout>
  )
}

function App() {
  return (
    <AuthProvider>
      <Dashboard />
    </AuthProvider>
  )
}

export default App
